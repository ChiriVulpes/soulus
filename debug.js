const exec = require("child_process").exec;

exec(`@echo off & start gradlew ${process.argv.includes("-server") ? "runServer" : "runClient"} --debug-jvm & timeout 20 & exit`);

setTimeout(() => {
	process.exit();
}, 20 * 1000);