/* eslint-disable no-undef */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.kickout = functions.https.onCall((data, context)=> {
  const topic = data.text;
  console.log(data.text);

  const payLoad = {
    notification: {
      title: "You have been kicked out!",
      body: "A manager has kicked you out of your current building",
      sound: "default",
    },
  };

  const options = {
    priority: "high",
    timeToLive: 60*60*2,
  };
  return admin.messaging().sendToTopic(topic, payLoad, options);
});

/* schedule syntax
minute         0-59
hour           0-23
day of month   1-31
month          1-12 (or names, see below)
day of week    0-7 (0 or 7 is Sunday, or use names)
*/

exports.scheduledFunctionCrontab = functions.pubsub.schedule('0 0 * * *') 
    .onRun((context) => {
        console.log('This will be run every day at 12:00 AM Pacific!');
        var checkouts = [];
        let buildings = new Map();
        getStudentUsers(checkouts, buildings);
        return null;
});

async function getStudentUsers(checkouts, buildings) { 
    const UserRef = admin.firestore().collection('users')
    const buildingRef = admin.firestore().collection('buildings')
    const db = admin.firestore();
    const batch = db.batch();
    var d = new Date();
    const epochDate = Date.now();
    var formalDate = d.toLocaleString();

    const snapshot = await UserRef.where('currentBuilding', '!=', "").get(); //all non-null or empty
    if(snapshot.empty){
        console.log("No matching documents");
        return;
    }
    snapshot.forEach(doc => {
        try {
            const deleted = doc.data().deleted;
            if (deleted == false){
                var recordObject = {buildingId: doc.data().currentBuilding, buildingName: "", 
                                    checkIn: false, epochTime: epochDate, major: doc.data().major, 
                                    studentId: doc.data().id, studentUid: doc.data().uid, 
                                    time: formalDate};
                checkouts.push(recordObject);
                batch.update(doc.ref, {currentBuilding: null});
            }
        } 
        catch (error) {
            console.log("snapshot1", e);
        }
    });

    const snapshot2 = await buildingRef.where('currentCapacity', '!=', 0).get(); //all non-null and non 0
    if(snapshot2.empty){
        console.log("No matching documents");
        return;
    }
    snapshot2.forEach(doc => {
        try {
            buildings.set(doc.data().id, doc.data().name); //get building ids
            batch.update(doc.ref, {currentCapacity: 0});
        } 
        catch (e) {
            console.log("snapshot2", e);
        }
    });

    const payLoad = {
        notification: {
          title: "You have been kicked out!",
          body: "A manager has kicked you out of your current building",
          sound: "default",
        },
    };
    
    const options = {
        priority: "high",
        timeToLive: 60*60*2,
    };
    
    for( i = 0; i<checkouts.length; i++){
        checkouts[i].buildingName = buildings.get(checkouts[i].buildingId); //set the Id
        admin.messaging().sendToTopic(checkouts[i].studentUid, payLoad, options);
        console.log("entered checkouts loop");
        var checkouts_object = JSON.parse(JSON.stringify(checkouts[i]));
        const res = await db.collection('records').add(checkouts_object);
    }

    await batch.commit();
}