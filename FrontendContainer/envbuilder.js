const fs = require("fs");
const target = "./src/environments/environment.ts";
const mode = process.argv?.[2] ?? "";

const isProduction = mode === "production";

const environment = {
  production: isProduction,
  javaHost: `http://${process.env.JAVA_HOST}:${process.env.JAVA_PORT}/apis`,
  chatHost: `ws://${process.env.PYTHON_HOST}:${process.env.PYTHON_PORT}`,
};

fs.writeFileSync(
  target,
  `export const environment = ${JSON.stringify(environment)}`
);
