//TODO commentare
//RESET BROWSE FILE show (will be deprecated)
function clearData(){
	var control = $("#dataset"),
	clearBn = $("#clear");
	clearBn.on("click", function(){
		control.replaceWith( control.val('').clone( true ) );
	});
	control.on({
		change: function(){ console.log("Changed") },
		focus: function(){ console.log("Focus") }
	});
}


/**
 * @param filename nome del file json dove raccogliere le info per disegnare la ROC curves e la AUC
 * @returns disegna i grafici dentro gli elementi html con ID graph1 e graph2
 * @help al momento utilizzata solamente nella pagine show.html
 * @TODO integrare la lettura dei dati dei vari test da disegnare direttamente da DB (potrebbe essere conveniente salvarsi ogni volta i risultati dei test nel DB)
 */
function Draw(result){

			var colorpalette = [
				[0, 166, 160], //verde acqua
				[138, 196, 62], //verde pisello
				[52, 59, 67], //grigio scuro
				[196, 43, 19], //rosso scarlatto
				[40, 71, 166], //blu scuro
				[255, 186, 58], //arancione chiaro
				[169, 52, 255], //lilla
				[59, 175, 255], //azzurro
				[79, 217, 21], //verde brillante
				[217, 145, 196] //rosa
				];

			//CREAZIONE DELL'INPUT PER GRAFICO ROC -------------------------------------
			var ROCcurves = []; //INPUT PER PLOT
			var count = 0;
			var testdetails = JSON.parse(localStorage.getItem("listJSON"));
			for(var i in result){
				var obj = result[i];
				var x = [];
				var y = [];
				for(var j in obj.fprTrace) x.push(obj.fprTrace[j]);
				for(var j in obj.tprTrace) y.push(obj.tprTrace[j]);
				var objJSON = testdetails[count];
				ROCcurves.push(
						{
							x: x,
							y: y,
							mode: "lines",
							name: objJSON.name,
							line: {
								"shape": "spline",
								"color": "rgb("+colorpalette[count][0]+","+colorpalette[count][1]+","+colorpalette[count][2]+")"
							}
						}
				);
				count++;
			}

			ROCcurves.push({
				x: [0.0, 1.0],
				y: [0.0, 1.0],
				mode: "lines",
				name: "tpf = fpr",
				line: {
					"dash": "dot",
					"color": "rgb(168, 168, 168)"
				}
			});

			//CREAZIONE DELL'INPUT PER GRAFICO AUC -------------------------------------
//			var AUCcurves = []; //INPUT PER PLOT
			var listAUC = [];
			for(var i in result) {
				var obj = result[i];
				var singleObj = {}
				singleObj['x'] = obj.trainingSize;
				singleObj['y'] = obj.AUC;
				listAUC.push(singleObj);
			};

			//sort to better interpolate AUC curve
			listAUC.sort(function(a, b) {						
				return ((a.x < b.x) ? -1 : ((a.x == b.x) ? 0 : 1));
			});

			var xAUC = [];
			var yAUC = [];
			for(var i in listAUC)
			{
				xAUC.push(listAUC[i].x);
				yAUC.push(listAUC[i].y);
			}
			//ADD HORIZONTAL AXIS AT 1
//			AUCcurves.push({
//				"x": [0.0, 3500],
//				"y": [1.0, 1.0],
//				"mode": "lines",
//				"name": "AUC = 1",
//				"line": {
//					"dash": "dot",
//					"color": "rgb(168, 168, 168)"
//				}
//			});
			//ADD POINTS OF THE AUC CURVE
			var AUCcurves = {
				x: xAUC,
				y: yAUC,
				mode: "splines",
				name: "AUC curve",
				line: {
					"dash": "dot",
					"color": "rgb("+colorpalette[count][4]+","+colorpalette[count][4]+","+colorpalette[count][4]+")"
				}
			};

			//LAYOUT GRAFICO ROC
			var layout1 = {
					legend: {
						x: 0.6,
						y: 20,
						font: {size: 12},
						yref: 'paper',
					},
					xaxis: {
						title: 'fpr',
						range: [0 , 1],
//						autorange: true
					},
					yaxis: {
						title: 'tpr',
						range: [0 , 1],
//						autorange: true
					},
					autosize: true,
//					  width: 400,
//					  height: 400,
					  margin: {
					    l: 70,
					    r: 50,
					    b: 80,
					    t: 50,
					    pad: 8
					  },
					  paper_bgcolor: '#f2f2f2',
					  plot_bgcolor: '#f2f2f2'
			};

			//LAYOUT GRAFICO AUC
			var layout2 = {
					showlegend: true,
					legend: {
						x: 0.5,
						y: 20,
//						traceorder: 'reversed',
						font: {size: 12},
						yref: 'paper',
					},
//					title: 'AUC Curve',
					xaxis: {
						title: 'N',
						autorange: true
					},
					yaxis: {
						title: 'AUC',
						range: [0,1],
						autorange: false
					},
					autosize: true,
//					  width: 400,
//					  height: 400,
					  margin: {
					    l: 70,
					    r: 50,
					    b: 80,
					    t: 50,
					    pad: 8
					  },
					  paper_bgcolor: '#f2f2f2',
					  plot_bgcolor: '#f2f2f2'
			};

			var data1 = [];
			var data2 = [AUCcurves];
			//PLOT GRAFICO ROC E AUC
			Plotly.newPlot('graph1', ROCcurves, layout1);
			Plotly.newPlot('graph2', data2, layout2);
}

function DrawHistogram(histogramNegative,histogramPositive){
		
	var negative = [];
	var positive = [];
	console.log(histogramNegative)
	for(var i=0;i<histogramNegative.length;i++)
		{
		negative.push(histogramNegative[i]);
		}
	
	console.log(histogramPositive)
	for(var i=0;i<histogramPositive.length;i++)
	{
	positive.push(histogramPositive[i]);
	}
	
	//ADD negative histogram
	var negativeTrace = {
		x: negative,
		type: "histogram",
		opacity: 0.5,
		name: "Negative distribution",
		marker: {
			"color": "green",
		},
	xbins:{
		start: 0,
		end: 1,
		size: 0.05		
	}
	};
	
	//ADD positive histogram
	var positiveTrace = {
		x: positive,
		type: "histogram",
		opacity: 0.6,
		name: "Positive distribution",
		marker: {
			"color": "red",
		},
		xbins:{
			start: 0,
			end: 1,
			size: 0.05		
		}
	};
	
	var data = [negativeTrace , positiveTrace];


		var layout = {
//		showlegend : true,
		legend : {
			x : 0.5,
			y : 20,
			font: {size: 12},
			yref: 'paper'
		},
//		title : 'Distribution',
		barmode : "overlay",
		xaxis : {
			title : 'scores',
			range : [0,1],
			autorange : false
		},
		yaxis : {
			title : 'frequency',
			autorange : true
		},
		  autosize: true,
//		  width: 400,
//		  height: 400,
		  margin: {
		    l: 70,
		    r: 50,
		    b: 80,
		    t: 50,
		    pad: 8
		  },
		  paper_bgcolor: '#f2f2f2',
		  plot_bgcolor: '#f2f2f2'
	};
	
	Plotly.newPlot('graph_histogram', data, layout);
}

//append images from json files
/**
 * @param filename nome del file json dove raccogliere le info per i parametri di accuracy e threshold
 * @returns permette di impostare i parametri accuracy e threshold al variare del menù a tendina
 */
function setParameters(result) {

			$("#accuracy").empty();
			$("#threshold").empty();
			$("#accuracy").html(result.accuracyOpt.toFixed(2));
			$("#threshold").html(result.thresholdOpt.toFixed(2));

}

/**
 * @param filename nome del file json dove raccogliere le info per le immagini da caricare (falsipositivi e falsinegativi)
 * @returns permette di visionare nella pagina show.html i falsi positivi e i falsi negativi con anche una modalità preview
 * @help you have to set two div element in your html page with specific id (gallery, myModal and modalcontent)
 */
function showGallery(result,inputgallery) {

	var img_path = "GetImage?image_id=";
			setGallery(inputgallery);
			
			$("#modalcontent" + GALLERY).empty();
			$("#gallery" + GALLERY).empty();

			var slidenumber = 1;
			var totalslide = result.length;

			//Aggiungo le immagini nella modalità preview (elemento div con id gallery)
			for ( var i in result) {
				var x = document.createElement("IMG");
				var obj = result[i];
				x.setAttribute("src", img_path + obj);
				x.setAttribute("onclick", 'openModal();currentSlide('+slidenumber+')');
				// TODO approfondire addEventListener anche per pezzo successivo
				x.addEventListener("click", function(event) {
					newImg(inputgallery, slidenumber);
					event.preventDefault();
				});
				x.setAttribute("class", "hover-shadow cursor");
				x.setAttribute("onclick", 'currentSlide('+slidenumber+')');
				document.getElementById("gallery" + GALLERY).appendChild(x);
				$('#modalcontent' + GALLERY).append(
						"<div class='mySlides" + GALLERY
								+ "'><div class='numbertext'>" + slidenumber
								+ " / " + totalslide
								+ "</div><img class='modal-img' src="
								+ img_path + obj + "></div>");
				slidenumber++;
			}

			//inserisco le frecce per scorrere la galleria in modalità ZOOM (elemento div con id modalcontent)
			var a_prev = document.createElement("a");
			var a_next = document.createElement("a");
			var div_caption = document.createElement("div");
			a_prev.setAttribute("class", "prev");
			a_next.setAttribute("class", "next");
			a_prev.appendChild(document.createTextNode("\u276C"));
			a_next.appendChild(document.createTextNode("\u276D"));
			a_prev.addEventListener("click", function(event) {
				prevImg(inputgallery);
				event.preventDefault();
			});
			a_next.addEventListener("click", function(event) {
				nextImg(inputgallery);
				event.preventDefault();
			});
			$('#modalcontent' + GALLERY).append(a_prev);
			$('#modalcontent' + GALLERY).append(a_next);
			
			//Aggiungo l'elemento div che conterrà la galleria di immagini da mostrare sotto l'immagine in ZOOM (elemento div con modalcontent)
			$('#modalcontent' + GALLERY).append("<div class='caption-container'><p id='caption" + GALLERY + "'></p></div>");
			slidenumber = 1;
			for ( var i in result) {
				var obj = result[i];

				var x = document.createElement("IMG");
				x.setAttribute("src", img_path + obj);
				x.addEventListener("click", function(event) {
					newImgZoom(inputgallery, slidenumber);
					event.preventDefault();
				});
				x.setAttribute("class", "demo" + GALLERY + " cursor");
				x.setAttribute("onclick", 'currentSlide('+slidenumber+')');
				x.setAttribute("name", GALLERY + " " + slidenumber);
				x.setAttribute("id", "ID "+ GALLERY + " " + slidenumber);
				var div = document.createElement("div");
				div.setAttribute("class", 'column-captions');
				div.appendChild(x);
				$('#modalcontent' + GALLERY).append(div);
				slidenumber++;
			}
}

function newImgZoom(inputgallery,slidenumber){
	setGallery(inputgallery);
	console.log("DENTRO ZOOM**********"+GALLERY);
	console.log("DENTRO ZOOM**********"+slidenumber);
	console.log("id "+ $(this).attr("id"));
	console.log("class "+ $(this).attr("class"));
	console.log("name "+ $(this).attr("name"));
	currentSlide(slidenumber);
	}

function newImg(inputgallery,slidenumber){
	setGallery(inputgallery);
	console.log("DENTRO**********"+GALLERY);
	console.log("DENTRO**********"+slidenumber);
	openModal();
	currentSlide(slidenumber);
	}

function prevImg(inputgallery){
	setGallery(inputgallery);
	console.log("DENTRO tasto freccia PREV**********"+GALLERY);
	plusSlides(-1);
}

function nextImg(inputgallery){
	setGallery(inputgallery);
	console.log("DENTRO tasto freccia NEXT**********"+GALLERY);
	plusSlides(1);
}

//Le seguenti funzioni sono a supporto della funzione addimages() ----------------
function openModal() {
	document.getElementById('myModal'+GALLERY).style.display = "block";
}


function closeModal() {
	document.getElementById('myModal'+GALLERY).style.display = "none";
}


function openModal2() {
	populateListTestResult();
	$('#savetestmodal').show("slow");
	$('#savetestmodalbackground').show("slow");
	$('#savetestmodalcontent').show("slow");	
}

function closeModal2() {
	$('#savetestmodal').hide("slow");
	$('#savetestmodalbackground').hide("slow");
	$('#savetestmodalcontent').hide("slow");
}

var slideIndex = 1;
var GALLERY = "";
function setGallery(galleryname){GALLERY=galleryname;};
showSlides(slideIndex);

function plusSlides(n) {
	showSlides(slideIndex += n);
}

function currentSlide(n) {
	showSlides(slideIndex = n);
}

function showSlides(n) {
	if (GALLERY == "") {
	    swal({
			title: 'Warning',
			text: 'You are trying to access a gallery that does not exist!',
			type: 'warning',});
	} else {
		var i;
		var slides = document.getElementsByClassName("mySlides" + GALLERY);
		var dots = document.getElementsByClassName("demo" + GALLERY);
		var captionText = document.getElementById("caption" + GALLERY);
		if (n > slides.length) {
			slideIndex = 1
		}
		if (n < 1) {
			slideIndex = slides.length
		}
		for (i = 0; i < slides.length; i++) {
			slides[i].style.display = "none";
		}
		for (i = 0; i < dots.length; i++) {
			dots[i].className = dots[i].className.replace(" active", "");
		}
		slides[slideIndex - 1].style.display = "block";
		dots[slideIndex - 1].className += " active";
		captionText.innerHTML = dots[slideIndex - 1].alt;
	}
}

function createGallery(idTOappend,images,idgallery)
{
	var gallery = "TestSetHome"+idgallery;
	var myGallery = document.createElement("div");
	myGallery.setAttribute("class","post-preview");
	myGallery.setAttribute("id","gallery"+gallery);
	
	var myModal = document.createElement("div");	
	myModal.setAttribute("class", "modal");
	myModal.setAttribute("id", "myModal"+gallery);
	
	var modalContent = document.createElement("div");
	modalContent.setAttribute("class","modal-content");
	modalContent.setAttribute("id","modalcontent"+gallery);
	
	var mySpan = document.createElement("span");
	mySpan.setAttribute("class","close cursor");
	mySpan.addEventListener("click", function(event) {
		closeModal();
		event.preventDefault();
	});
	mySpan.appendChild(document.createTextNode('\u2715'));

	myModal.appendChild(mySpan);
	myModal.appendChild(modalContent);
	
	$("#"+idTOappend+"").append(myGallery);
	$("#"+idTOappend+"").append(myModal);
	
	showGallery(images,gallery);
	}
//--------------------------------------------------------------------------

//TODO commentare
//GET DATA TO SHOW: questa dovrebbe essere la funzione che lega la richiesta dei test da mostrare in show.html???
//function getDataShow(){
//
//	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
//	// ajax call to backend
//	$.ajax(
//			{
//				url: "json/helicopter_test.json",
////				url: 'GetTestResult',
//				type: 'GET',
//				data:{ array: finalJSON },
//				dataType: 'json',
//				success: function(result)
//				{
//					$("#waiting").fadeOut(1000);
//					$("#showtest").fadeIn(2000);
//					localStorage.setItem("resultJSON", JSON.stringify(result));
//					printShowPage();
//				}
//			});
//
//}


/**
 * @returns prepara il file JSON con gli input e richiama la funzione getDataShow() per ottenere i risultati dal backend
 */
function startSimulation(){
	
			    var testName = Array.prototype.map.call($("[id*=testname]"),(function(el){return el.value;}));
				var testDataset = Array.prototype.map.call($("[id*=testset]"),(function(el){return el.value;}));
				var testClassifier = Array.prototype.map.call($("[id*=testclassifier]"),(function(el){return el.value;}));
			    var testname;
			    
			    directJSON = [];
			    for( k = 0; k < TOTALROWSFILLED; k++ ){
			       	item = {}
			    	item["name"] = testName[k];
			    	item["test"] = testDataset[k];
			    	item["classifier"] = testClassifier[k];
			    	directJSON.push(item); 
			    }  
			    
			    localStorage.setItem("listJSON",JSON.stringify(directJSON));
			    
			    $("#simulate").fadeOut(1000);
			    setTimeout(function(){$("#waiting").fadeIn(1000)},1000);

				var testdetails = JSON.parse(localStorage.getItem("listJSON"));
				// ajax call to backend
				$.ajax(
						{
							url: "json/helicopter_test.json",
//							url: 'GetTestResult',
							type: 'GET',
							data:{ array: testdetails },
							dataType: 'json',
							success: function(result)
							{
								$("#waiting").fadeOut(1000);
								$("#showtest").fadeIn(2000);
								localStorage.setItem("resultJSON", JSON.stringify(result));
								printShowPage();
							}
						});
	}

function printTestResults(){
	
	var checkedValue = Array.prototype.map.call($(".formcheckbox:checked"),(function(el) {return el.value;}));
	var result = JSON.parse(localStorage.getItem("resultJSON"));
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
	var count=0;
	
	//costruisco il JSON da stampare
	directJSON = [];
    for(var i in result){
    	var objtest = testdetails[i];
    	var obj = result[i];
    	if(objtest.name==checkedValue[count])
    		{
    		directJSON.push(obj); 
    		count++;
    		}
    }

    var json = JSON.stringify(directJSON);
    var blob = new Blob([json], {type: "application/json"});
    saveAs(blob, "TestResult.json");
    closeModal2();
}

function populateListTestResult(){
	$("#listatest").empty();
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
	
	for(var i in testdetails){
		var obj = testdetails[i];
		var label = document.createElement("label");
		var input = document.createElement("input");
		var linebreak = document.createElement("br");
		
		
		input.value = obj.name;
		input.type = "checkbox";
		input.setAttribute("class","formcheckbox")
		label.appendChild(input);
		label.appendChild(document.createTextNode("  "+obj.name));

		$("#listatest").append(label);
		$("#listatest").append(linebreak);
	}
	
	var input = document.createElement("input");
	input.type = "submit";
	$("#listatest").append(input);
}



/**
 * @returns popola i menu a tendina della pagine di set-up
 */
function populateSelectSim(){

	// chiamata per popolare il menu a tendina dei classificatori ready nella pagina simulate.html
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		//url: "json/classifier.json",
		url: 'GetClassifier',
		async: false,
		success: function(result)
		{
			// fill classifier drop down menu (only if status: ready)
			for(var j in result){
				var obj = result[j];
				if(obj.status == "ready"){
					$('[id*="testclassifier"]').append($('<option>', {
						value: obj._id,
						text: obj.label+" "+obj.training_size
					}));
				}
			}
		}
	});
	
	// chiamata per popolare il menu a tendina dei testset nella pagina simulate.html
	$.ajax({													
		dataType: "json",
		//url: "json/testset.json",
		url: 'GetDataset',
		data: 'sub_type=test_set',
		async: false,
		success: function(result)
		{
			// fill test set and cathegory drop down menu (only if status: ready)
			for(var i in result){
				var obj = result[i];
	
					$('[id*="testset"]').append($('<option>', {
						value: obj._id,
						text: obj.label+" "+ (obj.images.positive.length + obj.images.negative.length)
					}));
			}

		}
	});
	
}

/**
 * @param IDelement: id dell'elemento html dove esporre la tabella
 * @param table: array[row][col][elements] che verrà esposta in formato tabella (per ogni coordinata si possono stampare fino a 3 elementi)
 * @returns aggiunge una tabella ad un elemento div basandosi sull'input di un array di array
 */
function addTable(IDelement,table){
	//Create a HTML Table element.
	var tableElement = document.createElement('table');
	var columnCount = table[0].length;
	var rowCount = table.length;
	//Add the data rows.
	for (var i = 0; i < rowCount; i++) {
		//row = tableElement.insertRow(-1);
		var row = document.createElement('tr');
		for (var j = 0; j < columnCount; j++) {

			if(i==0 || j==0){
				var txt = document.createTextNode(table[i][j][0]);
				var th = document.createElement('th');
				th.appendChild(txt);
				row.appendChild(th);
			}
			else{
				var td = document.createElement('td');
				for(var k=0;k<3 & table[i][j][k]!="";k++)
				{
					var block = document.createElement('div');
					block.className = 'block';
					block.appendChild(document.createTextNode(table[i][j][k]));
					td.appendChild(block);
				}
				if(table[i][j][0]=="") td.appendChild(document.createTextNode(table[i][j][0]));
				row.appendChild(td);
			}
		}
		tableElement.appendChild(row);
	}
	document.getElementById(IDelement).appendChild(tableElement);
}


function addMatrixTable(IDelement,table){
	var tableElement = document.createElement('table');
	var columnCount = table[0].length;
	var rowCount = table.length;
	for (var i = 0; i < rowCount; i++) {
		var row = document.createElement('tr');
		for (var j = 0; j < columnCount; j++) {
			if(i==0){
				var txt = document.createTextNode(table[i][j]);
				var th = document.createElement('th');
				th.appendChild(txt);
				row.appendChild(th);
			}
			else{
				var td = document.createElement('td');
				var block = document.createElement('div');
				//block.className = 'block';
				block.appendChild(document.createTextNode(table[i][j]));
				td.appendChild(block);
				row.appendChild(td);
			}
		}
		tableElement.appendChild(row);
	}
	document.getElementById(IDelement).appendChild(tableElement);
	}

// variante tabella con solo la prima colonna evidenziata
function addTableColumn(IDelement,table,trainingsize){
	//Create a HTML Table element.
	var tableElement = document.createElement('table');
	var columnCount = table[0].length;
	var rowCount = table.length;
	//Add the data rows.
	for (var i = 0; i < rowCount; i++) {
		//row = tableElement.insertRow(-1);
		var row = document.createElement('tr');
		for (var j = 0; j < columnCount; j++) {

			if(j==0){
				var txt = document.createTextNode(table[i][j][0]);
				var th = document.createElement('th');
				th.style.width = "30px";
				var block = document.createElement('div');
				block.appendChild(txt);
				th.appendChild(block);
				row.appendChild(th);
			}
			else{
				var td = document.createElement('td');
				td.style.width = "30px";
				for(var k=0;k<3 & table[i][j][k]!="";k++)
				{
					var block = document.createElement('div');
					
					if(table[i][j][k]=="ready") block.className = 'smoothrectangle ready';
					if(table[i][j][k]=="training") block.className = 'smoothrectangle training';
					if(table[i][j][k]=="zombie") block.className = 'smoothrectangle zombie';			
					
					block.appendChild(document.createTextNode(trainingsize[j-1]+"_"+"v"+k));
					td.appendChild(block);
				}
				if(table[i][j][0]=="") td.appendChild(document.createTextNode(""));
				row.appendChild(td);
			}
		}
		tableElement.appendChild(row);
	}
	document.getElementById(IDelement).appendChild(tableElement);
	
	$('.ready').attr("data-tooltip", "Classifier ready");	
	$('.training').attr("data-tooltip", "Classifier training");
	$('.zombie').attr("data-tooltip", "Classifier zombie");
}


function addMatrixTable(IDelement,table){
	var tableElement = document.createElement('table');
	var columnCount = table[0].length;
	var rowCount = table.length;
	for (var i = 0; i < rowCount; i++) {
		var row = document.createElement('tr');
		for (var j = 0; j < columnCount; j++) {
			if(i==0){
				var txt = document.createTextNode(table[i][j]);
				var th = document.createElement('th');
				th.appendChild(txt);
				row.appendChild(th);
			}
			else{
				var td = document.createElement('td');
				var block = document.createElement('div');
				//block.className = 'block';
				block.appendChild(document.createTextNode(table[i][j]));
				td.appendChild(block);
				row.appendChild(td);
			}
		}
		tableElement.appendChild(row);
	}
	document.getElementById(IDelement).appendChild(tableElement);
	}

/**
 * @returns unica funzione che permette la costruzione della pagina home.html grazie a tre chiamate ajax
 * @help servlet interrogate: GetInstance, GetClassifier, GetDataset
 */
function generateHome(){

	//**************************************************************************
	//********* GET FROM THE SERVER VRINSTANCES ********************************
	//**************************************************************************
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		url: 'GetInstance',
		async: false,
		success: function(result){
			var free = 0;
			console.log(result);
			for(var i in result)
			{
				var obj = result[i].classifiers;
				if(obj.length == 0) free++;
			}
			$('#freeclass').html(free);
		}
	});

	//**************************************************************************
	//********* GET FROM THE SERVER CLASSIFIER *********************************
	//**************************************************************************
	$.ajax({
		contentType: "application/json",
		dataType: "json",
//		url: "json/classifier2.json",
		url: 'GetClassifier',
		async: false,
		success: function(result){

			//COMPUTE NUMBER FOR READY AND TRAINING
			var ready = 0;
			var training = 0;
			var zombie = 0;

			for(var i in result)
			{
				var obj = result[i];
				if(obj.status == "ready") ready++;
				if(obj.status == "training") training++;
				if(obj.status == "zombie") zombie++;
			}
			$('#readyclass').html(ready);
			$('#trainingclass').html(training);

			//PRINT TABLE OF CLASSIFIERS
			var label = [];
			var n_img = [];
			for(var i in result)
			{
				var obj = result[i];
				if(label.indexOf(obj.label) == -1) label.push(obj.label);
				if(n_img.indexOf(obj.training_size) == -1) n_img.push(obj.training_size);
			}
			n_img.sort(function(a, b){return a - b;});
			
			//Inizializzazione matrice dei classificatori
			var sizeRow=label.length;
			var sizeCol=n_img.length;
			var matrix = new Array(sizeRow);
			for (var i = 0; i < sizeRow; i++) {
				matrix[i] = new Array(sizeCol);
				for(var j = 0; j < sizeCol; j++){
					matrix[i][j] = new Array(3);
					for(var k = 0; k < 3; k++) matrix[i][j][k] = "";
				}
			}

			//Inizializzazione matrice da stampare (classificatori + label + cardinality)
			var print_table = new Array(sizeRow);
			for (var i = 0; i < sizeRow; i++) {
				print_table[i] = new Array(sizeCol+1);
				for(var j=0;j<(sizeCol+1);j++) print_table[i][j] = new Array(3);
			}
			//Riempimento della matrice più interna
			for(var i in result)
			{
				var obj = result[i];
				var n = label.indexOf(obj.label); //row
				var m = n_img.indexOf(obj.training_size); //col
				var k = matrix[n][m].indexOf("");
				matrix[n][m][k]=obj.status;
			}
			//inizializza header delle label
			//print_table[0][0][0] = "Cardinality";
			for(var i = 0; i < sizeRow; i++)	print_table[i][0][0] = label[i];
			//for(var i = 0; i < sizeCol; i++) print_table[0][i+1][0] = n_img[i];
			//imposta la matrice dei contenuti da stampare
			for(var i = 0; i < sizeRow; i++)
				for(var j = 0; j < sizeCol; j++)
					for(var k = 0; k < 3; k++)
						print_table[i][j+1][k] = matrix[i][j][k];

			//add a table (idelement and table to print)
			addTableColumn("dvTable",print_table,n_img);
		}
	});

	//**************************************************************************
	//********* GET FROM THE SERVER TRAININGDATASETS ***************************
	//**************************************************************************
	$.ajax({
		contentType : "application/json",
		dataType : "json",
//		 url: "json/dataset.json",
		url : 'GetDataset',
		data : 'sub_type=test_set',
		async : false,
		success : function(result) {
			for ( var i in result) {
				
				var circle = document.createElement("div");
				circle.setAttribute("class","smoothrectangle ready");
				circle.setAttribute("id","showdatasetID"+result[i]._id);
				circle.appendChild(document.createTextNode(result[i]._id));
				$("#listdataset").append(circle);
				
			}
		}
	});
	
	// **************************************************************************
	//********* GET FROM THE SERVER TESTSET ***************************
	//**************************************************************************
//	$.ajax({
//		contentType: "application/json",
//		dataType: "json",
//		//url: "json/dataset.json",
//		url: 'GetDataset',
//		data: 'sub_type=test_set',
//		async: false,
//		success: function(result){
//			
//			console.log(result);
//for(var i in result)
//	{
//			var gallery = "TestSetHome"+result[i]._id;
//			var myGallery = document.createElement("div");
//			myGallery.setAttribute("class","post-preview");
//			myGallery.setAttribute("id","gallery"+gallery);
//			
//			var myModal = document.createElement("div");	
//			myModal.setAttribute("class", "modal");
//			myModal.setAttribute("id", "myModal"+gallery);
//			
//			var modalContent = document.createElement("div");
//			modalContent.setAttribute("class","modal-content");
//			modalContent.setAttribute("id","modalcontent"+gallery);
//			
//			var mySpan = document.createElement("span");
//			mySpan.setAttribute("class","close cursor");
//			mySpan.addEventListener("click", function(event) {
//				closeModal();
//				event.preventDefault();
//			});
//			mySpan.appendChild(document.createTextNode("CLOSE"));
//
//			myModal.appendChild(mySpan);
//			myModal.appendChild(modalContent);
//			
//			$('#testSetGallery').append(myGallery);
//			$('#testSetGallery').append(myModal);
//			
//			showGallery(result[i].images.negative,gallery);		
//		}
//
//		}
//	});

}

function checkTestName() {
	selectArray = Array.prototype.map.call($(".moltiplicandum input"),(function(el) {return el.value;}));
	var sizeinput = selectArray.length;
	var check = 1;
	var num = 0;
	for (var i = 0; i < sizeinput & check==1; i++) {
		var num1 = i + 1;
		var pass = $('input[name=test' + num1 + ']').val();
		if(pass=="")
			{
			alert("Test name cannot be empty!");
			check = 0;
			}
		for (var j = 0; j < sizeinput & check==1; j++) {
			if (j != i) {
				var num2 = j+1;
				var repass = $('input[name=test' + num2 + ']').val();
				if (pass == repass) {
					console.log("*************")
					console.log(pass)
					console.log(repass)
				console.log("*************")
					alert("Different tests cannot have the same name!");
					check = 0;
				} 
			}
		}
	}
	return check;
}

function checkSelectedInput() {

	selectArray = Array.prototype.map.call($(".moltiplicandum select"),
			(function(el) {
				return el.value;
			}));
	var check = 1;
	var sizeinput = selectArray.length;

	for (var i = 0; i < sizeinput & check==1; i++) {
		if (selectArray[i] == "") {
			alert("Test Set and Classifier must be selected for each test!");
			check = 0;
		}
	}
	return check;
}


function createBlockTest(IDappend,testname,label,classifier){
	
	var block = document.createElement("div");
	block.setAttribute("class","blocktest");
	
//	======================
	var blocktest = document.createElement("div");
	blocktest.setAttribute("class","blocktestattribute");
	
	var pblockT1 = document.createElement("p");
	pblockT1.setAttribute("class","smalltitle");
	pblockT1.appendChild(document.createTextNode(testname));
	
	var pblockP1 = document.createElement("p");
	pblockP1.setAttribute("class","paragraph");
	pblockP1.appendChild(document.createTextNode("LABEL: "+label));
	
	var pblockP2 = document.createElement("p");
	pblockP2.setAttribute("class","paragraph");	
	pblockP2.appendChild(document.createTextNode("CLASSIFIER: "+classifier));
	
	blocktest.appendChild(pblockT1);
	blocktest.appendChild(pblockP1);
	blocktest.appendChild(pblockP2);
//	===========================
	var blockicon1 = document.createElement("div");
	blockicon1.setAttribute("class","blocktesticon");
	
	var icon1 = document.createElement("img");
	icon1.setAttribute("class","icon verysmall blocktest");
	icon1.setAttribute("src","ico/garbageDARK.png");
	icon1.setAttribute("id","garbage"+testname);
	
	blockicon1.appendChild(icon1);
	
	var blockicon2 = document.createElement("div");
	blockicon2.setAttribute("class","blocktesticon");
	
	var icon2 = document.createElement("img");
	icon2.setAttribute("class","icon verysmall blocktest");
	icon2.setAttribute("src","ico/plus-symbol.png");
	icon2.setAttribute("id","plus"+testname);
	
	blockicon2.appendChild(icon2);
//	===========================
	block.appendChild(blocktest);
	block.appendChild(blockicon1);
	block.appendChild(blockicon2);
	
	$("#"+IDappend+"").append(block);
}


function printShowPage() {
	
	//carica i file da local storage
	var result = JSON.parse(localStorage.getItem("resultJSON"));
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));

	//verifica che non ci siano vuoti e costruisce il select test panel
	var testcount = 0;
	for ( var j in result) {
		var obj = result[j];
		if (obj.ID == null) {
			swal({
				title : "Warning",
				imageUrl : "img/tired.png",
				text : "Classifier "
						+ testdetails[testcount].classifier
						+ " is exhausted. Wait 24h and you will regain your free API calls",
			});
		} else {
			$('#show_test').append($('<option>', {
				value : testdetails[testcount].name,
				text : testdetails[testcount].name
			}));
		}
		testcount++;
	}

	//disegna i grafici comuni a più test
	Draw(result);

	//disegna gli oggetti dipendenti dal test selezionato
	var testname = $("#show_test").val();
	for ( var j in testdetails) {
		if (testdetails[j].name == testname) {
			setParameters(result[j]);
			showGallery(result[j].falseNegativeOpt, "FN");
			showGallery(result[j].falsePositiveOpt, "FP");
			DrawHistogram(result[j].histogramNegative,result[j].histogramPositive);
		}
	}
}

function updateTestFields() {
	
	//carica i file da local storage
	var result = JSON.parse(localStorage.getItem("resultJSON"));
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));

	//disegna gli oggetti dipendenti dal test selezionato
	var testname = $("#show_test").val();
	for ( var j in testdetails) {
		if (testdetails[j].name == testname) {
			setParameters(result[j]);
			showGallery(result[j].falseNegativeOpt, "FN");
			showGallery(result[j].falsePositiveOpt, "FP");
			DrawHistogram(result[j].histogramNegative,result[j].histogramPositive);
		}
	}
}
