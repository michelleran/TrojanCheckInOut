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

exports.scheduledFunctionCrontab = functions.pubsub.schedule('0 0 * * *') //12 am every day  //uncomment this when you want it to actually work
//exports.scheduledFunctionCrontab = functions.pubsub.schedule('10 19 * * *') //7:10 pm PST every day (testing purposes)
    //.timeZone('America/Los_Angeles') // set time zone (default is already LA)
    .onRun((context) => {
        console.log('This will be run every day at 12:00 AM Pacific!');
        const UIDSet = new Set()
        getStudentUsers(UIDSet);
        return null;
});



async function getStudentUsers(UIDSet) { 
    const UserRef = admin.firestore().collection('users')//admin.firestore.collection('users') 
    const buildingRef = admin.firestore().collection('buildings')//admin.firestore.collection('buildings')
    //let bulkWriter = firestore.bulkWriter();
    const db = admin.firestore();
    const batch = db.batch();
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

    const snapshot = await UserRef.where('currentBuilding', '!=', "").get(); //all non-null or empty
    if(snapshot.empty){
        console.log("No matching documents");
        return;
    }
    snapshot.forEach(doc => {
        try {
            const deleted = doc.data().deleted;
            if (deleted == false){
                batch.update(doc.ref, {currentBuilding: null});
            }
        } 
        catch (error) {
            
        }
    });

    const snapshot2 = await buildingRef.where('currentCapacity', '!=', "0").get(); //all non-null and non 0
    if(snapshot2.empty){
        console.log("No matching documents");
        return;
    }
    snapshot2.forEach(doc => {
        try {
            batch.update(doc.ref, {currentCapacity: 0});
        } 
        catch (e) {
            console.log("snapshot2", e);
        }
    });
    await batch.commit();
}