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
