import { createValidateInterceptor } from "@connectrpc/validate";
import { fastify } from "fastify";
import { fastifyConnectPlugin } from "@connectrpc/connect-fastify";
import routes from "./connect.js";
import cors from "@fastify/cors";

async function main() {
  const server = fastify();

  await server.register(cors, {
    origin: "http://localhost:5173",
    methods: ["GET", "POST", "OPTIONS"],
  });

  await server.register(fastifyConnectPlugin, {
    interceptors: [createValidateInterceptor()],
    routes,
  });


  await server.listen({ host: "localhost", port: 8080 });
  console.log("server is listening at", server.addresses());
}

main();