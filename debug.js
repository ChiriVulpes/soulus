const childProcess = require("child_process");
const fs = require("fs");

childProcess.exec(`@echo off & start /WAIT debug.bat ${process.argv.includes("-server") ? "runServer" : "runClient"}`);

fs.writeFileSync("compile_log.txt", "");

setInterval(() => {
	const fileText = fs.readFileSync("compile_log.txt", "utf16le");
	if (fileText.includes("Listening for transport dt_socket at address"))
		process.exit();
}, 200);