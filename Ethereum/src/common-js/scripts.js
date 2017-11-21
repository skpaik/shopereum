
(function ($) {

    "use strict";

	// $(window).on('beforeunload', function(){
	  // $("html,body").scrollTop(0);
	// });
	
	// Close the div if user clicks outside of the div
	$(window).on('click', function() {
		$('.dark-bg').removeClass('visible');
		$('.visible-on-click').removeClass('visible');
	});
	
	// Prevent the closing the div if user clicks inside of the div
	$('.visible-on-click').on('click', function(event){
		event.stopPropagation();
	});

	// Close button for right side hidden area
	$('.close-btn').on('click', function(){
		$('.dark-bg').removeClass('visible');
		var closeElem = $(this).data('close');
		$(closeElem).removeClass('visible');
		
	});
	
	// Click event for displaying the div that has "data-nav-menu" attribute (Search area, Smaller device menu, Right hidden area)
	$('[data-nav-menu]').on('click', function(event){
		
		var $this = $(this),
			visibleHeadArea = $this.data('nav-menu');
		
		$('.nav-visible').not(visibleHeadArea).removeClass('visible');
		
		$(visibleHeadArea).toggleClass('visible');
		
		if($(visibleHeadArea).hasClass('visible')){ $('.dark-bg').addClass('visible'); }
		else{ $('.dark-bg').removeClass('visible'); }
		
		event.stopPropagation();
	
	});
	
	
	loadPgeEffect();
	
	navMenuFn();
	
	
	$("a[href='#']").on('click', function(event){
		return false;
	});
	
	
	 $('[data-toggle="tooltip"]').tooltip(); 
	
	
	searchBtnFn();
	
	btnHoverEffect();
	
	enableBSCarousel();
	
	BSCarouselIndicators();

	enableSwiper();
	
	courseViewFn();
	
	var el = $(window),
		lastY = el.scrollTop();
	
	
	// MAKE MAIN MENU FIXED ON SCROLL
	
	var mainMenuId = isExists('#main-menu-bar') ? $('#main-menu-bar') : null;
	var mainMenuBottom = (mainMenuId == null) ? 0 : mainMenuId.offset().top + mainMenuId.outerHeight();
	menuFixedOnScroll( mainMenuId, mainMenuBottom );
	
	$(window).on('scroll', function(){
		
		var top_of_window = $(window).scrollTop(),
			bottom_of_window = $(window).scrollTop() + $(window).height(),
			winHeight =  $(window).height();
			
		paralaxBkgd(top_of_window, winHeight);
		
		// ENABLE COUNTER
		
		var counterId = isExists('.counter') ? $('.counter') : null;
		var counter_top = (counterId == null) ? 0 : counterId.offset().top ;
		
		if( (top_of_window <= counter_top) && (bottom_of_window >= counter_top) ){
			counterEnable('.counter-area');
		}
		
		
		//FIXED MENU ON SCROLL UP
		
		 var currY = el.scrollTop(),
        
		// determine current scroll direction
		direction = (currY > lastY) ? 'down' : ((currY === lastY) ? 'none' : 'up');

		menuFixedOnScroll( mainMenuId, mainMenuBottom, direction);
		
		// update last scroll position to current position
		lastY = currY;
		
		
	});//EOF SCROLL
	
	var winWidth = $(window).width();
	var isTouchDevice = (winWidth <= 767) ? true : false;
	dropdownFn(isTouchDevice);
	
	$(window).on('resize', function(){
		
		winWidth = $(window).width();
		isTouchDevice = (winWidth <= 767) ? true : false;
		
		debounceFuncForDropDown(isTouchDevice);
	
	});
	

})(jQuery);

function courseViewFn(){
	
	var animEndEv = 'webkitAnimationEnd animationend',
		$courseNav = $('[data-course-nav]'),
		courseDelay;
	$courseNav.on('click', function(){
		
		clearInterval(courseDelay);
		
		var $this = $(this),
			target = $this.data('course-nav'),
			$courseArea = $this.parents('.courses-area'),
			$course = $courseArea.find('.course'),
			bsClass = $this.data('course-bs-class');
		
		$courseNav.removeClass('active');
		$this.addClass('active');
		$course.addClass('full-opacity').on(animEndEv, function(){
			$course.removeClass('full-opacity');
		});
		
		courseDelay = setInterval( function(){  
			if(target === 'horizontal'){
				$courseArea.addClass('horizontal-course'); 
				$course.parent().removeClass().addClass(bsClass);
			}else{
				$courseArea.removeClass('horizontal-course'); 
				$course.parent().removeClass().addClass(bsClass);
			}
		
		}, 300);
		
	});
	
}

function searchBtnFn(){
	
	$('[data-search-target]').on('click', function(event){
		
		var $this = $(this),
			target = $this.data('search-target');
		
		$this.find('.search-icn').toggleClass('no-size');
		
		$this.find('.close-icn').toggleClass('full-size');
		
		$(target).toggleClass('active');
		
		if($(target).hasClass('active')) $(target).focus();
		
		return false;
		
	});
	
}
function paralaxBkgd(topOfWindow, winHeight){
	
	var top_of_window = topOfWindow,
		bottom_of_window = topOfWindow + winHeight,
		middle_of_window = topOfWindow + winHeight/2;
	
	// TRANSLATE PARALLAX BACKGROUND ON SCROLL
	
	$('[data-paralax-bg]').each(function(){
		var $this = $(this),
			scrollSpeed = $this.data('bg-animation-speed');
			top_of_elem = $this.offset().top;
			bottom_of_elem = $this.offset().top + $this.outerHeight();
			middle_of_elem = $this.offset().top + $this.outerHeight()/2;
		
		var transformSize = Math.round((top_of_window - top_of_elem)/scrollSpeed);
		
		if( (top_of_window < bottom_of_elem) && (bottom_of_window > top_of_elem) ){
			$this.css({ 'background-position-y' : transformSize +'px' });
		}
	});	
	
}

var debounceFuncForDropDown = debounce(function( isTouchDevice ) {
	dropdownFn(isTouchDevice);
},50);

function dropdownFn(isTouchDevice){
	
	var $dropDown = $('.drop-down'),
		$dropDownA = $('.drop-down').children('a');
	
	if(isTouchDevice){
		
		$dropDownA.on('click', function(e){
			
			var $this = $(this),
				$dropDown = $this.parent('.drop-down'),
				clicks = $this.data('clicks');
			
			if (clicks) $dropDown.removeClass('active');
			else $dropDown.addClass('active');
			
			$this.data("clicks", !clicks);
			
			return false;
			
		});
		
	}else if(!isTouchDevice){
		
		$dropDown.removeClass('active')
		
		$dropDown.on('mouseenter mouseleave', function(e){
			
			var $this = $(this);
			
			if(e.type === 'mouseenter') $this.addClass('active');
			else if(e.type === 'mouseleave') $this.removeClass('active');
			
		});
	}
}

function navMenuFn(){
	
	$('[data-toggle=navigation]').on('click', function(){
		
		var $this = $(this);
		var mainMenu = $this.data('target');
		
		$this.toggleClass('close-btn');
		$(mainMenu).toggleClass('open');
		
		return false;
		
	});
	
}


function menuFixedOnScroll( mainMenuId, mainMenuBottom, direction ){
	
	var top_of_window = $(window).scrollTop();
	
	if(top_of_window >= mainMenuBottom){
	
		if(direction === 'down'){
			$(mainMenuId).addClass('menu-out');
		}else if(direction === 'up'){ 
			$(mainMenuId).addClass('fixed-menu');
			$(mainMenuId).removeClass('menu-out');
		}
		
	}else{
		$(mainMenuId).removeClass('fixed-menu'); 
	}
	
}


function showProjectFunc(){
	
	var closeHandle;
	
	var animEndEv = 'webkitAnimationEnd animationend';
	
	$('[data-project-target]').on('click', function(){ 
		
		clearInterval(closeHandle);
		
		var projectId = $(this).data('project-target');
		var $item = $(this).parents('.item');
		
			closeHandle = setInterval(function(){
			
			$(projectId).addClass('bg-color');
			
		}, 1000);
		
		$(projectId).addClass('visible');
		$item.removeClass('project-sm-trans-to-top').removeClass('project-sm-trans-to-bottom').addClass('project-sm-trans-to-top');

		
		$(projectId).find('.project-wrapper').addClass('project-to-bottom', 100).on(animEndEv, function(){
			$(this).removeClass('project-to-bottom');
		});
		
	});
	
	
	$('.close-project-btn').on('click', function(){ 
		
		var projectId = $(this).parents('.projects');
		var $item = $(projectId).prev('.item');
		
		$(projectId).removeClass('bg-color');
		
		clearInterval(closeHandle);
		closeHandle = setInterval(function(){
			
			$(projectId).removeClass('visible');
		
			$item.removeClass('project-sm-trans-to-top').removeClass('project-sm-trans-to-bottom').addClass('project-sm-trans-to-bottom');

			
			$(projectId).find('.project-wrapper').addClass('project-to-top', 100).on(animEndEv, function(){
				$(this).removeClass('project-to-top');
			});
			
		}, 1000);
		
	});
	
}

function buttonClickEffect(){
	
	$('a').not('[href^="#"]').on('click', function(e){
		
		e.preventDefault();
		var goTo = this.getAttribute("href");
		var currentFile = window.location.href;
		
		// CHECKING IF THE CLICKED LINK IS THE CURRENT PAGE
		if(currentFile.indexOf(goTo) > -1) { return false; }
		
		var top = e.pageY;
		var left = e.pageX;
		
		$('body').append('<div class="fixed-circle"></div>');
		
			$('.fixed-circle').css({
				'left' : left + 'px',
				'top' : top  + 'px'
			});	
		
		setTimeout(function(){
			window.location = goTo;
		},1000);
		
	});
}

function btnHoverEffect(){
	
	var animEndEv = 'webkitAnimationEnd animationend';
	var animationCompleted = false; 
	$('[data-hover-effect]').on('mouseenter', function(e){
		animationCompleted = false; 
		
		$('.btn-child').remove();
		
		var offset = $(this).offset();
		var top = (e.pageY - offset.top);
		var left = (e.pageX - offset.left);
			
		$(this).append('<div class="btn-child"></div>');
		$('.btn-child').css({
			'left' : left + 'px',
			'top' : top  + 'px'
		});
		
		
		$(this).find('.btn-child').addClass('in', 100).on(animEndEv, function(){
			animationCompleted = true;
		});
	}).on('mouseleave', function(e){
		if(animationCompleted){
			$(this).find('.btn-child').addClass('out', 100).on(animEndEv, function(){
				$(this).removeClass('in');
			});
			
		}else{
			$(this).find('.btn-child').addClass('in', 100).on(animEndEv, function(){
				$(this).addClass('out');
			});
		}
		
	});
	
}

function loadPgeEffect(){
	
	$(window).on('load', function() {
		var $cssLoader = $('.css-loader');
		var $loaderWritings = $('.loader-wrapper');
		
		setTimeout(function() {
			$cssLoader.addClass('after-load');
		}, 500);
    });
	
}


function scrollWheelEvtForBS(){
	var isMoving = false;
	
	   $(document).on('swipeleft', function() {
			alert()
		});
	
	$('#main-carousel').unbind('mousewheel DOMMouseScroll').on('mousewheel DOMMouseScroll', function(e) {
		
		e.preventDefault();
		
		var mainCarousleId = $('#main-carousel');
		
		if (isMoving) return;
		isMoving = true;
		setTimeout(function() { isMoving = false; },1500);
		
		var delta = parseInt(e.originalEvent.wheelDelta || -e.originalEvent.detail);
		 
		if(delta/120 > 0) 	$(mainCarousleId).carousel('prev');
		else 	$(mainCarousleId).carousel('next'); 
		
	}); 
}


function BSCarouselIndicators(){
	
	var $carouselIndicators = $('.carousel-indicators');
	var carouselId = '#' + $carouselIndicators.parent('.carousel').attr('id');
	
	var $items = $('.carousel-inner .item');
	$($items).each(function(index){
		
		var activeted = "";
		if($(this).hasClass('active')){ activeted = 'active'; }
		$carouselIndicators.append('<li data-target="'+ carouselId + '" data-slide-to="' + index + 
			'" class="'+ activeted +'"></li>');
		
	});
	
}

function doAnimations( elems , direction ) {
	
    //Cache the animationend event in a variable
	
    var animEndEv = 'webkitAnimationEnd animationend';
    
	elems.each(function() {
	
		var $this = $(this),
			animationType = $this.data('slider-animation'),
			$innerAnimationElem = $this.find('.inner-animation'),
			animationDelay = $this.data('animation-delay'),
			animationDuration = $this.data('animation-duration'),
			inverseAnimtion = 'inverse-' + animationType;
			
		$innerAnimationElem.removeClass('scale-up');
			
		$this.addClass(animationType, 100).css({
			'animation-duration' : animationDuration,
			'animation-delay' : animationDelay
		}).on(animEndEv, function() {
			$this.removeClass(animationType);
		});
		
		$innerAnimationElem.css({
			'animation-duration' : animationDuration,
			'animation-delay' : animationDelay
			
		}).addClass(inverseAnimtion, 100).on(animEndEv, function() {
			if($innerAnimationElem.data('slider-scale-up')){
				$innerAnimationElem.addClass('scale-up');
			}
			$innerAnimationElem.removeClass(inverseAnimtion);
		});
		
	});
}

function enableBSCarousel(){
	
	var interval = $('#main-carousel').data('slider-interval');
	
	$('#main-carousel').carousel({
        interval: (interval ? interval : 10000)
    });
	
	var $myCarousel = $('.carousel'),
		$firstAnimatingElems = $myCarousel.find('.item:first').find("[data-slider-animation]");
		
	doAnimations($firstAnimatingElems);
	
	$myCarousel.on('slide.bs.carousel', function (e) {
		
		var $currentElem = $(e.relatedTarget),
			direction = e.direction,
			$animatingElems = $currentElem.find("[data-slider-animation]");
		
        doAnimations($animatingElems, direction);
		
    });
	
	
}


function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
}


function counterEnable(elem){
	
	$($(elem).find('.counter-value')).each(function() {
		var $this = $(this),
			countDuration = $this.data('duration'),
			countTo = $this.data('count');
			
		$({
			countNum: $this.text()
		}).animate({
			countNum: countTo
		},
		{	
			duration: countDuration,
			easing: 'linear',
			step: function() {
				$this.text(Math.floor(this.countNum));
			},
			complete: function() {
				$this.text(this.countNum);
			}

		});
	});
}


function enableMasonary(){
	
	if ( $.isFunction($.fn.masonry) && isExists('.masonry-container')) {
		$('.masonry-container').each(function(){
			$(this).masonry({ itemSelector: '.masonry-item',  columnWidth: '.masonry-item', percentPosition: true });
		});
	}
	
}

function enableSwiper(){
	
	if ( isExists('.swiper-container') ) {
		
		$('.swiper-container').each(function (index) {
			
			var swiperDirection 			= $(this).data('swiper-direction'),
				swiperSlidePerView			= $(this).data('swiper-slides-per-view'),
				swiperBreakpoints			= $(this).data('swiper-breakpoints'),
				swiperSpeed					= $(this).data('swiper-speed'),
				swiperCrossFade				= $(this).data('swiper-crossfade'),
				swiperLoop					= $(this).data('swiper-loop'),
				swiperAutoplay 				= $(this).data('swiper-autoplay'),
				swiperMousewheelControl 	= $(this).data('swiper-wheel-control'),
				swipeSlidesPerview 			= $(this).data('slides-perview'),
				swiperMargin 				= parseInt($(this).data('swiper-margin')),
				swiperSlideEffect 			= $(this).data('slide-effect'),
				swiperAutoHeight 			= $(this).data('autoheight'),
				swiperScrollbar 			= ($(this).data('scrollbar') ? $(this).find('.swiper-scrollbar') : null);
				swiperScrollbar 			= (isExists(swiperScrollbar) ? swiperScrollbar : null);
				
			
			var swiper = new Swiper($(this)[0], {
				pagination			: $(this).find('.swiper-pagination'),
				
				
				slidesPerView		: ( swiperSlidePerView ? swiperSlidePerView : 1 ),
				direction			: ( swiperDirection ? swiperDirection : 'horizontal'),
				loop				: ( swiperLoop ? swiperLoop : false),
				nextButton			: '.swiper-button-next',
				prevButton			: '.swiper-button-prev',
				autoplay			: ( swiperAutoplay ? swiperAutoplay : false),
				paginationClickable	: true,
				spaceBetween		: ( swiperMargin ? swiperMargin : 0),
				mousewheelControl	: ( (swiperMousewheelControl) ? swiperMousewheelControl : false),
				scrollbar			: ( swiperScrollbar ? swiperScrollbar : null ),
				scrollbarHide		: false,
				speed				: ( swiperSpeed ? swiperSpeed : 1000 ),
				autoHeight			: ( (swiperAutoHeight == false) ? swiperAutoHeight : true ),
				effect				: ( swiperSlideEffect ? swiperSlideEffect : 'coverflow' ),
				fade				: { crossFade: swiperCrossFade ? swiperCrossFade : false },
				breakpoints			: {
											1200: { slidesPerView: swiperBreakpoints ? 3 : 1, },
											992: { slidesPerView: swiperBreakpoints ? 2 : 1, },
											580: { slidesPerView: 1, }
											
										},
				
										
				
			});
			
		});
		
	}
}

function isExists(elems){
	if ($(elems).length > 0) { 
		return true;
	}
	return false;
}





