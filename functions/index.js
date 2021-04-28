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

// eslint-disable-next-line max-len
exports.scheduledFunctionCrontab = functions.pubsub.schedule("0 0 * * *") // 12 am every day  //uncomment this when you want it to actually work
    // .timeZone('America/Los_Angeles') // set time zone (default is already LA)
    .onRun((context) => {
      console.log("This will be run every day at 12:00 AM Pacific!");
      const UIDSet = new Set();
      getStudentUsers(UIDSet);
      return null;
    });


// eslint-disable-next-line require-jsdoc
async function getStudentUsers(UIDSet) {
  const UserRef = firestore.collection("users");
  const buildingRef = firestore.collection("buildings");
  const bulkWriter = firestore.bulkWriter();
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

  UserRef.where("currentBuilding", "!=", "null")
      .where("deleted", "==", "false").get().then((querySnapshot) => {
        querySnapshot.forEach((documentSnapshot) => {
          console.log(`Found document at ${documentSnapshot.ref.path}`);
          const documentRef = firestore.doc(documentSnapshot.ref);
          const UID = documentSnapshot.get("uid");


          UIDSet.add(UID);
          // try {
          //     await db.runTransaction(async (t) => {
          //       const doc = await t.get(documentRef);
          //       t.update(documentRef, {"currentBuilding": "null"})
          //     });
          //     console.log('Transaction Success!');
          // }
          // catch (e) {
          //     console.log('Transaction Failure:', e);
          // }
          // call austin's function using the UID
          admin.messaging().sendToTopic(UID, payLoad, options);
          // Server.sendToTopic(UID);


          bulkWriter
              // eslint-disable-next-line max-len
              .update(documentRef, {currentBuilding: "null"}) // maybe check this
              .then((result) => {
                console.log("Successfully executed write at: ", result);
              })
              .catch((err) => {
                console.log("Write failed with: ", err);
              });
        });
      });


  buildingRef.where("currentCapacity", "!=", 0).get().then((querySnapshot) => {
    querySnapshot.forEach((documentSnapshot) => {
      console.log(`Document found at path: ${documentSnapshot.ref.path}`);
      const documentRef = firestore.doc(documentSnapshot.ref);
      // try {
      //     await db.runTransaction(async (t) => {
      //         const doc = await t.get(documentRef);
      //         t.update(documentRef, {"currentCapacity": 0})
      // });
      // console.log('Transaction Two Success!');
      // }
      // catch (e) {
      //     console.log('Transaction Two Failure:', e);
      // }
      bulkWriter
          .update(documentRef, {currentCapacity: 0}) // maybe check this
          .then((result) => {
            console.log("Successfully executed write at: ", result);
          })
          .catch((err) => {
            console.log("Write failed with: ", err);
          });
    });
  });
}
