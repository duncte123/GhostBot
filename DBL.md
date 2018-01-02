<script>
  //Might add this later
  //document.querySelectorAll(".logo>img")[0].src="https://cdn.discordapp.com/emojis/394148311835344896.png";

  var btns = document.querySelectorAll(".btn");
  for(var btn in btns) {
  	btns[btn].className += " grow smooth";
  }
  //document.getElementById("upvotecounterprofile").className += " grow smooth";
  var thing =  document.querySelectorAll(".bot-img>img")[0];
  thing.addEventListener("click", function() {
  	window.open('https://discord.gg/NKM9Xtk', '_blank');
  });
</script>
<style>
  span.servers {
  	background: #6ffe32;
  }
  #menu {
  	background-color: #4BB020 !important;
  	box-shadow: 0 0 8px rgba(65, 68, 99, .8);
  }
  .grow,
  .grow-less,
  .grow-more,
  .smooth {
  	transition: all .2s ease-in-out;
  }

.grow:hover {
    transform: scale(1.1);
 }

.grow-less:hover {
	transform: scale(1.05);
}

.grow-more:hover {
	transform: scale(1.2);
}
</style>
# GhostBot
GhostBot is a discord bot made for Danny Phantom fans.

Current Command list:
```yaml
gb.goingghost: Screams "going ghost" in the voice channel that you are in (has a 5% chance of becoming ghostly)
gb.wail: Gives you a nice ghostly wail
gb.fuitloop: You're one crazed up fruitloop
gb.help: Your avarage help command
gb.about: Gives some information about the bot
```

It currently only includes voice commands and more commands will be added soon.
If you have any ideas for commands to add contact _duncte123#1245_ or join the support guild.