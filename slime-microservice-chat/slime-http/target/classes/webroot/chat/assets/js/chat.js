var ChannelType = {
	NEW_USER: "/user",
	NEW_MESSAGE: "/message",
	OFFLINE: "/offline"
};
var serverIp = "localhost";

var page = {
		
	sockJSClient: null,
	
	$msgList: $("#lstMsg"),
	$msgModel: $("#msgModel").detach().removeAttr("id"),
	
	$userList: $("#lstUsers"),
	$userModel: $("#userModel").detach().removeAttr("id"),
	
	init: function() {
		page.initView();
		page.bindEvent();
	},
	
	initView: function() {
		
	},
	
	connect: function(userId, successCallback) {
		   var url = sprintf("http://%s:8383/sockjs", serverIp, userId)
		   page.sockJSClient = new vertx.EventBus(url);
           
		   page.sockJSClient.onopen = function () {
			   page.sockJSClient.login(userId, "password", function(response) {
				   successCallback(response);
			   });
		   };
	},
	
	bindEvent: function() {
		
		$("#txtName").keyup(function(e){
		    if(e.keyCode == 13)
		    {
		    	$("#btnJoin").click();
		    }
		});
		$("#btnJoin").on("click", function() {
			var userId = $("#txtName").val();
			
			page.connect(userId, function(response){
				
				$(".loginDiv").hide();
				$(".container").show();
				
				var users = response.list;
				$.each(users, function(index, user){
					page.renderUser({userId: user});
				});
				
				// emphasize current user
				page.$userList.find("#" + userId).find("h5").css("font-weight", "bold").css("font-style", "italic")
				
				
				// handle event of server close current connection
				page.sockJSClient.onclose = function(){
					$(".loginDiv").show();
					$(".container").hide();
				};
				
				
				var channel = "topic/chat".concat(ChannelType.NEW_MESSAGE);
				page.sockJSClient.registerHandler(channel, function(msg){
                	page.renderMessage(msg)
            	});
				
				
				var channel = "topic/chat".concat(ChannelType.NEW_USER);
				page.sockJSClient.registerHandler(channel, function(user){
                	page.renderUser(user);
            	});
				
				var channel = "topic/chat".concat(ChannelType.OFFLINE);
				page.sockJSClient.registerHandler(channel, function(user){
                	page.removeUser(user);
            	});

			})
		});
		
		
		
		$("#txtMessage").keyup(function(e){
		    if(e.keyCode == 13)
		    {
		    	$("#btnSend").click();
		    }
		});
		
		$("#btnSend").on("click", function(){
			var $txtMessage = $("#txtMessage");
			var msg = $txtMessage.val();
			$txtMessage.val("");
			
			var address = "topic/chat".concat(ChannelType.NEW_MESSAGE);
			page.sockJSClient.publish(address, msg, function(response) {});
		});
	},
	
	renderMessage: function(msg) {
		var $msgModel = page.$msgModel.clone();
    	$msgModel.find(".lblMsg").text(msg.message);
    	$msgModel.find(".lblUser").text(msg.sender);
    	page.$msgList.append($msgModel);
	},
	
	renderUser: function(user) {
		var $userModel = page.$userModel.clone();
		$userModel.prop("id", user.userId);
		$userModel.find(".lblUser").text(user.userId);
    	page.$userList.append($userModel);
	},
	
	removeUser: function(user) {
		page.$userList.find("#" + user.userId).remove();
	}
};

$(document).ready(function(){
	page.init();
	
	window.onbeforeunload = function (event) {
		
		// demonstrate before closing browser, 
		// no need to call this, disconnect is called when tab/browser is close
		if(page.sockJSClient != null) {
			page.sockJSClient.close();
		}
	};

})