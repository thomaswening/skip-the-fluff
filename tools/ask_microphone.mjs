// Asks for the microphone from inside the app's WebView, as a speaking exercise does, and prints
// Android's answer as the website receives it. Used to check
// [REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038) and
// [REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)
// on an emulator without finding a speaking exercise. How to use it, and how to reset the permission between runs:
// https://github.com/thomaswening/skip-the-fluff/blob/main/docs/development.md#microphone-check
//
// Needs Node 22 (its built-in fetch and WebSocket), no packages. Before running it, forward the
// WebView's DevTools socket to the computer:
//   adb forward tcp:9222 localabstract:webview_devtools_remote_<pid of the app>

const DEFAULT_PORT = 9222;

// Exit codes as in diff(1) and grep(1): 0 success, 1 negative result, 2 the check could not run.
const EXIT_GRANTED = 0;
const EXIT_REFUSED = 1;
const EXIT_CANNOT_RUN = 2;

// The learner's answer in Android's dialog is part of the request, so the wait covers a person
// tapping, or a second shell tapping with uiautomator.
const ANSWER_TIMEOUT_MILLISECONDS = 120_000;

// The DevTools protocol matches each answer to its request by this number. The script sends one.
const EVALUATE_REQUEST_ID = 1;

// Runs in the page. It releases the microphone again at once, so the next run asks afresh, and
// turns a refusal into its error name, e.g. NotAllowedError.
const ASK_FOR_MICROPHONE = `
  navigator.mediaDevices.getUserMedia({ audio: true }).then(
    (stream) => { stream.getTracks().forEach((track) => track.stop()); return "granted"; },
    (error) => error.name)
`;

const port = Number(process.argv[2] ?? DEFAULT_PORT);
if (!Number.isInteger(port)) {
  console.error("usage: node tools/ask_microphone.mjs [port]");
  process.exit(EXIT_CANNOT_RUN);
}

try {
  const page = await findWebsitePage(port);
  const answer = await askForMicrophone(page.webSocketDebuggerUrl);
  console.log(answer);
  process.exit(answer === "granted" ? EXIT_GRANTED : EXIT_REFUSED);
} catch (error) {
  console.error(`cannot run: ${error.message}`);
  process.exit(EXIT_CANNOT_RUN);
}

/**
 * Returns the DevTools target of the page the WebView shows.
 * Throws if nothing listens on the port, or if the WebView shows no page.
 */
async function findWebsitePage(port) {
  const listUrl = `http://127.0.0.1:${port}/json/list`;
  let response;
  try {
    response = await fetch(listUrl);
  } catch {
    throw new Error(`nothing answers on ${listUrl}; is the app running and the socket forwarded?`);
  }
  const targets = await response.json();
  const page = targets.find((target) => target.type === "page");
  if (!page) throw new Error(`the WebView shows no page; targets: ${JSON.stringify(targets)}`);
  return page;
}

/**
 * Runs getUserMedia in the page and returns "granted" or the error's name.
 * Throws if the page reports an exception or no answer comes within the timeout.
 */
function askForMicrophone(webSocketUrl) {
  return new Promise((resolve, reject) => {
    const socket = new WebSocket(webSocketUrl);
    const timer = setTimeout(() => {
      socket.close();
      reject(new Error(`no answer within ${ANSWER_TIMEOUT_MILLISECONDS / 1000} s; is Android's dialog still open?`));
    }, ANSWER_TIMEOUT_MILLISECONDS);

    socket.addEventListener("open", () => {
      socket.send(JSON.stringify({
        id: EVALUATE_REQUEST_ID,
        method: "Runtime.evaluate",
        params: { expression: ASK_FOR_MICROPHONE, awaitPromise: true, returnByValue: true },
      }));
    });
    socket.addEventListener("message", (event) => {
      const message = JSON.parse(event.data);
      // Events the page sends on its own carry no id. Only the answer to the request counts.
      if (message.id !== EVALUATE_REQUEST_ID) return;
      clearTimeout(timer);
      socket.close();
      const failure = message.error?.message ?? message.result.exceptionDetails?.text;
      if (failure) reject(new Error(failure));
      else resolve(message.result.result.value);
    });
    socket.addEventListener("error", () => {
      clearTimeout(timer);
      reject(new Error(`cannot connect to ${webSocketUrl}`));
    });
  });
}
