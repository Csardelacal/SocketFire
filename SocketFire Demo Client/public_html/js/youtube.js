
// 2. This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

//Get the size of the player area
var playerarea = document.querySelector('.player');
var width = playerarea.clientWidth;
var height = playerarea.clientHeight;
var playing = false;


// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
var player;
function onYouTubeIframeAPIReady() {
  player = new YT.Player('ytplayer', {
	 height: height,
	 width: width,
	 videoId: 'UMcA6NvJXmk',
	 events: {
		'onReady': onPlayerReady,
		'onStateChange': admin? onPlayerStateChange : null
	 }
  });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
  //Wait for the user to play
}

function onPlayerStateChange(event) {
  if (event.data == YT.PlayerState.PLAYING && !playing) {
	  console.log("Play");
	  playing = true;
	  window.s.sendChannelMessage('play', [""+player.getCurrentTime()]);
  }
  if (event.data == YT.PlayerState.PAUSED) {
	  console.log("Pause");
	  window.s.sendChannelMessage('pause', [""+player.getCurrentTime()]);
	  playing = false;
  }
}
function stopVideo() {
  player.stopVideo();
}