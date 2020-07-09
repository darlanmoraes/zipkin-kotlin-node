const express = require("express");
const http = require("http");
const zipkin = require('zipkin-instrumentation-express').expressMiddleware;
const { HttpLogger } = require("zipkin-transport-http");
const { Tracer, BatchRecorder, ExplicitContext, jsonEncoder: { JSON_V2 } } = require("zipkin");

const {
  ZIPKIN_SERVER, APP_NAME, PORT
} = process.env;

const tracer = new Tracer({
  ctxImpl: new ExplicitContext(),
  recorder: new BatchRecorder({
    logger: new HttpLogger({
      endpoint: `${ZIPKIN_SERVER}api/v2/spans`,
      jsonEncoder: JSON_V2
    })
  }),
  localServiceName: APP_NAME
});

const router = express.Router();
const app = express();
app.use(router);
app.use(express.json());
app.use(zipkin({ tracer }));

app.get("/status", (req, res) => {
  res.sendStatus({
    status: "okay"
  });
});

app.post("/message", (req, res) => {
  res.send(req.body);
});

const server = http.createServer(app);

server.listen(PORT, () => {
  console.log(`Server on: http://0.0.0.0:${PORT}`);
});