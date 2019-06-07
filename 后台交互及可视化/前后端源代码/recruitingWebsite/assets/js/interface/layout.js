(function(){
		//阻止冒泡事件
		function stopBubble(e) { 
			//如果提供了事件对象，则这是一个非IE浏览器 
			if ( e && e.stopPropagation ) 
				//因此它支持W3C的stopPropagation()方法 
				e.stopPropagation(); 
			else 
				//否则，我们需要使用IE的方式来取消事件冒泡 
				window.event.cancelBubble = true; 
		}
		//阻止浏览器默认行为
		function stopDefault( e ) { 
			//阻止默认浏览器动作(W3C) 
			if ( e && e.preventDefault ) 
				e.preventDefault(); 
			//IE中阻止函数器默认动作的方式 
			else 
				window.event.returnValue = false; 
			return false; 
		}
		$(".hovera").on("mouseover",function(event){
			stopDefault();
			stopBubble();
//			console.log($(this).next());
			
			$(this).next().fadeIn();
			
		});
		$(".hoverli").on("mouseleave",function(event){
			stopDefault();
			stopBubble();
//			console.log($(this).next());
			
			$(this).children(".submenu").fadeOut();
			
		});
		
		
	})();