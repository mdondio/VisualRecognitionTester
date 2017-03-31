var cacheTestResult;
var finalJSON;

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
			var parsedJSON = JSON.parse(finalJSON);
			for(var i in result){
				var obj = result[i];
				var x = [];
				var y = [];
				for(var j in obj.fprTrace) x.push(obj.fprTrace[j]);
				for(var j in obj.tprTrace) y.push(obj.tprTrace[j]);
				var objJSON = parsedJSON[count];
				console.log(parsedJSON)
				console.log(objJSON.name)
				ROCcurves.push(
						{
							"x": x,
							"y": y,
							"mode": "lines",
							"name": objJSON.name,
							"line": {
								"shape": "spline",
//								"color": "rgb(168,168,168)"
							"color": "rgb("+colorpalette[count][0]+","+colorpalette[count][1]+","+colorpalette[count][2]+")"
							}
						}
				);
				count++;
			}

			ROCcurves.push({
				"x": [0.0, 1.0],
				"y": [0.0, 1.0],
				"mode": "lines",
				"name": "tpf = fpr",
				"line": {
					"dash": "dot",
					"color": "rgb(168, 168, 168)"
				}
			});

			//CREAZIONE DELL'INPUT PER GRAFICO AUC -------------------------------------
			var AUCcurves = []; //INPUT PER PLOT
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
			AUCcurves.push({
				"x": xAUC,
				"y": yAUC,
				"mode": "splines",
				"name": "AUC curve",
				"line": {
					"dash": "dot",
					"color": "rgb("+colorpalette[count][4]+","+colorpalette[count][4]+","+colorpalette[count][4]+")"
				}
			});

			//LAYOUT GRAFICO ROC
			var layout1 = {
					showlegend: false,
//					legend: {
//						x: 1,
//						y: 1,
//						traceorder: 'reversed',
//						font: {size: 16},
//						yref: 'paper',
//					},
					//title: 'ROC Curve',
					xaxis: {
						title: 'fpr',
//						range: [0, 1],
						autorange: true
					},
					yaxis: {
						title: 'tpr',
//						range: [0, 1],
						autorange: true
					},
					  autosize: false,
					  width: 400,
					  height: 400,
					  margin: {
					    l: 50,
					    r: 50,
					    b: 100,
					    t: 100,
					    pad: 4
					  },
					  paper_bgcolor: '#f2f2f2',
					  plot_bgcolor: '#f2f2f2'
			};

			//LAYOUT GRAFICO AUC
			var layout2 = {
					shoelegend: false,
//					legend: {
//						x: 1,
//						y: 1,
//						traceorder: 'reversed',
//						font: {size: 16},
//						yref: 'paper',
//					},
					//title: 'AUC Curve',
					xaxis: {
						title: 'N',
//						range: [0, 350],
						autorange: true
					},
					yaxis: {
						title: 'AUC',
//						range: [0, 1],
						autorange: true
					},
					  autosize: true,
					  width: 400,
					  height: 400,
					  margin: {
					    l: 50,
					    r: 50,
					    b: 100,
					    t: 100,
					    pad: 4
					  },
					  paper_bgcolor: '#f2f2f2',
					  plot_bgcolor: '#f2f2f2'
			};

			//PLOT GRAFICO ROC E AUC
			Plotly.newPlot('graph1', ROCcurves, layout1);
			Plotly.newPlot('graph2', AUCcurves, layout2);
}

function DrawHistogram(histogramNegative,histogramPositive){
		
	var scores = [];
	var negative = [];
	var positive = [];
	for(var i=0;i<100;i++)
		{
		negative.push(Math.random());
		positive.push(Math.random());
//		negative.push(histogramNegative[i]);
//		positive.push(histogramPositive[i]);
		}
	
	//ADD negative histogram
	var negativeTrace = {
		x: negative,
		type: "histogram",
		opacity: 0.5,
		name: "Negative distribution",
		marker: {
			"color": "green",
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
		}
	};
	
	var data = [negativeTrace , positiveTrace];


		var layout = {
		showlegend : false,
//		legend : {
//			x : 1,
//			y : 1
//		},
		title : 'Distribution',
		barmode : "overlay",
		xaxis : {
			title : 'scores',
			autorange : true
		},
		yaxis : {
			title : 'frequency',
			autorange : true
		},
		  autosize: false,
		  width: 400,
		  height: 400,
		  margin: {
		    l: 50,
		    r: 50,
		    b: 100,
		    t: 100,
		    pad: 4
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
				// TODO approfondire addEventListener anche per pezzo successivo
				x.addEventListener("click", function(event) {
					newImg(inputgallery, slidenumber);
					event.preventDefault();
				});
				x.setAttribute("class", "hover-shadow cursor");
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
			a_prev.appendChild(document.createTextNode("B"));
			a_next.appendChild(document.createTextNode("N"));
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
		swal('Oops...',
				'You are trying to access a gallery that does not exist!',
				'error')
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
	mySpan.appendChild(document.createTextNode("CLOSE"));

	myModal.appendChild(mySpan);
	myModal.appendChild(modalContent);
	
	$("#"+idTOappend+"").append(myGallery);
	$("#"+idTOappend+"").append(myModal);
	
	showGallery(images,gallery);
	}
//--------------------------------------------------------------------------

//TODO commentare
//GET DATA TO SHOW: questa dovrebbe essere la funzione che lega la richiesta dei test da mostrare in show.html???
function getDataShow(){

	// ajax call to backend
	$.ajax(
			{
				url: "json/testresult2.json",
//				url: 'GetTestResult',
				type: 'GET',
				data:{ array: finalJSON },
				dataType: 'json',
				success: function(result)
				{
					$("#waiting").fadeOut(1000);
					$("#showtest").fadeIn(2000);
					cacheTestResult = result;
					parsedJSON = JSON.parse(finalJSON)
					var testcount = 0;
					for(var j in result)
						{
						var obj = result[j];
						console.log(obj.ID);
						if(obj.ID==null) {
							swal({
							title: "Warning",
							imageUrl: "img/tired.png",
							text: "Classifier "+parsedJSON[testcount].classifier+" is exhausted. Wait 24h and you will regain your free API calls",
							});	
						}else{
							$('.show_test').append($('<option>', {
								value: parsedJSON[testcount].name,
								text: parsedJSON[testcount].name
							}));
						}
						testcount++;
						}
					
					Draw(result);
					
					var testname = $(".show_test").val();
					var parsedJSON = JSON.parse(finalJSON);
					for ( var j in parsedJSON) {
						if (parsedJSON[j].name == testname) {
							setParameters(result[j]);
							showGallery(result[j].falseNegativeOpt, "FN");
							showGallery(result[j].falsePositiveOpt, "FP");
							DrawHistogram(cacheTestResult[j].histogramNegative,
									cacheTestResult[j].histogramPositive);
						}
					}
				}
			});

}

//TODO commentare
// POPULATE SIMULATION DROP DOWN MENUS
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
					$('.avail_class').append($('<option>', {
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
	
					$('.test_set').append($('<option>', {
						value: obj._id,
						text: obj.label+" "+ (obj.images.positive.length + obj.images.negative.length)
					}));
			}

		}
	});
	
	// TODO: attenzione che questo blocco sotto non può stare qui, altrimenti si invoca
	// la GetTestResult
	// chiamata per popolare il menu a tendina dei test eseguiti nella pagina show.html
	// LENTISSIMO, CREARE FUNZIONE A PARTE O METTERE PARAMETRO
	
//	$.ajax({													
//		dataType: "json",
//		//url: "json/testresult.json",
//		url: 'GetTestResult',
//		async: false,
//		success: function(result)
//		{
//			//update show page test set
//			for(var j in result){
//				var obj = result[j];
////				console.log(obj.ID);
//				$('.show_test').append($('<option>', {
//					value: obj.ID,
//					text: obj.ID
//				}));
//			}
//		}
//	});
	
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
				if(i==0) th.style.borderTopWidth = "3px";
				var block = document.createElement('div');
				block.className = 'blockfirstcolumn';
				block.appendChild(txt);
				th.appendChild(block);
				row.appendChild(th);
			}
			else{
				var td = document.createElement('td');
				td.style.width = "30px";
				if(i==0) td.style.borderTopWidth = "3px";
				for(var k=0;k<3 & table[i][j][k]!="";k++)
				{
					var block = document.createElement('div');
					
					if(table[i][j][k]=="ready") block.className = 'blockready'; 
					else block.className = 'blocktraining';
					
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
			$('.free').html(free);
		}
	});

	//**************************************************************************
	//********* GET FROM THE SERVER CLASSIFIER *********************************
	//**************************************************************************
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		//url: "json/classifier.json",
		url: 'GetClassifier',
		async: false,
		success: function(result){

			//COMPUTE NUMBER FOR READY AND TRAINING
			var ready = 0;
			var training = 0;

			for(var i in result)
			{
				var obj = result[i];
				if(obj.status == "ready") ready++;
				else training++
			}
			$('.ready').html(ready);
			$('.training').html(training);

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
		// url: "json/dataset.json",
		url : 'GetDataset',
		data : 'sub_type=test_set',
		async : false,
		success : function(result) {
			for ( var i in result) {
				
				var circle = document.createElement("div");
				circle.setAttribute("class","circle3");
				circle.setAttribute("id","showdatasetID"+result[i]._id);
				
				var ready = document.createElement("div");
				ready.setAttribute("class","ready3");
				ready.appendChild(document.createTextNode(result[i]._id));
				circle.appendChild(ready);
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