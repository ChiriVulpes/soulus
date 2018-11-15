const exec = require("child_process").exec;

exec("start gradlew runClient --debug-jvm");

setTimeout(() => {
	process.exit();
}, 20 * 1000);