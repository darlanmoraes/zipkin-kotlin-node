const express = require("express");
const http = require("http");
const zipkin = require("zipkin-instrumentation-kafkajs");
const CLSContext = require('zipkin-context-cls');
const { Kafka } = require("kafkajs");
const { HttpLogger } = require("zipkin-transport-http");
const { Tracer, BatchRecorder, jsonEncoder: { JSON_V2 }, TraceId } = require("zipkin");

const {
  ZIPKIN_SERVER, KAFKA_SERVER, APP_NAME, PORT, KAFKA_TOPIC_1_NAME, KAFKA_TOPIC_2_NAME
} = process.env;
const CONSUMER_TOPIC = "TOPIC_1";
const PRODUCER_TOPIC = "TOPIC_2";

const router = express.Router();
const app = express();

app.use(router);
app.use(express.json());

const ctxImpl = new CLSContext(APP_NAME, true);

const tracer = new Tracer({
  ctxImpl: ctxImpl,
  recorder: new BatchRecorder({
    logger: new HttpLogger({
      endpoint: `${ZIPKIN_SERVER}api/v2/spans`,
      jsonEncoder: JSON_V2
    })
  }),
  localServiceName: APP_NAME
});

const kafka = new Kafka({
  clientId: APP_NAME,
  brokers: [ KAFKA_SERVER ]
});

const consumer = zipkin(kafka, {
  tracer,
  remoteServiceName : KAFKA_TOPIC_1_NAME
}).consumer({ groupId: APP_NAME });
consumer.connect();

const producer = zipkin(kafka, {
  tracer,
  remoteServiceName : KAFKA_TOPIC_2_NAME
}).producer();
producer.connect();

app.get("/status", (req, res) => {
  res.sendStatus({
    status: "okay"
  });
});

consumer.subscribe({ topic: CONSUMER_TOPIC });
consumer.run({
  eachMessage: async ({ topic, partition, message }) => {
    await producer.send({
      topic: PRODUCER_TOPIC,
      messages: [{
        value: message.value.toString(),
      }]
    });
  }
});

const server = http.createServer(app);

server.listen(PORT, () => {
  console.log(`Server on: http://0.0.0.0:${PORT}`);
});