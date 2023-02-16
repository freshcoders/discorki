// Get the DOM elements for the counters
const serverCount = document.getElementById("serverCount");
const summonerCount = document.getElementById("summonerCount");
const matchCount = document.getElementById("matchCount");
const matchInProgressCount = document.getElementById("matchInProgressCount");
const stats = document.getElementById("stats");

// Make the AJAX request to the /api/info endpoint
const xhr = new XMLHttpRequest();
xhr.open("GET", "/api/info");
xhr.onreadystatechange = function () {
  if (xhr.readyState === XMLHttpRequest.DONE) {
    if (xhr.status === 200) {
      // Parse the JSON response
      const response = JSON.parse(xhr.responseText);
      // Update the values in the HTML
      serverCount.innerText = response.servers;
      summonerCount.innerText = response.summoners;
      matchCount.innerText = response.matches;
      matchInProgressCount.innerText = response.matchesInProgress;
      stats.style.display = "block";
    } else {
      console.error("Failed to get the info", xhr.status);
    }
  }
};
xhr.send();
