//TODO commentare
// READ FILENAME FROM BROWSE FILE show (will be deprecated)
function retrieveData(){
	var nomefile = "json/"+$("#dataset").val(); 	// retrieve filename
	Draw(nomefile);											// call Draw function
}

//TODO commentare
// RESET BROWSE FILE show (will be deprecated)
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
function Draw(nomefile){

	$.ajax({
		dataType: "json",
		url: nomefile,
		success: function(result){

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
		for(var i in result.tests){
			var obj = result.tests[i];
			var x = [];
			var y = [];
			for(var j in obj.fpr) x.push(obj.fpr[j]);
			for(var j in obj.tpr) y.push(obj.tpr[j]);
			ROCcurves.push(
				{
					"x": x,
					"y": y,
					"mode": "lines",
					"name": obj.ID,
					"line": {
						"shape": "spline",
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
		for(var i in result.tests) {
			var obj = result.tests[i];
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
		AUCcurves.push({
			"x": [0.0, 3500],
			"y": [1.0, 1.0],
			"mode": "lines",
			"name": "AUC = 1",
			"line": {
				"dash": "dot",
				"color": "rgb(168, 168, 168)"
			}
		});
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
			legend: {
				y: 0.5,
				traceorder: 'reversed',
				font: {size: 16},
				yref: 'paper',
			},
			title: 'ROC Curve',
			xaxis: {
				title: 'fpr',
				range: [0, 1],
				autorange: false
			},
			yaxis: {
				title: 'tpr',
				range: [0, 1],
				autorange: false
			}
		};

		//LAYOUT GRAFICO AUC
		var layout2 = {
			legend: {
				y: 0.5,
				traceorder: 'reversed',
				font: {size: 16},
				yref: 'paper',
			},
			title: 'AUC Curve',
			xaxis: {
				title: 'N',
				range: [0, 350],
				autorange: false
			},
			yaxis: {
				title: 'AUC',
				range: [0, 1],
				autorange: false
			}
		};

		//PLOT GRAFICO ROC E AUC
		Plotly.newPlot('graph1', ROCcurves, layout1);
		Plotly.newPlot('graph2', AUCcurves, layout2);
	}
});
}

//append images from json files
/**
 * @param filename nome del file json dove raccogliere le info per le immagini da caricare (falsipositivi e falsinegativi)
 * @returns permette di visionare nella pagina show.html i falsi positivi e i falsi negativi con anche una modalità preview
 * @help al momento utilizzata solamente nella pagina show.html. 
 * @TODO integrare la lettura delle immagini direttamente da object storage con query da DB
 */
function addimages(filename){
	$.ajax({													
		dataType: "json",
		url: filename,
		success: function(result){

			$("#modalcontent").empty();
			$("#falsepositive").empty();
			$("#falsenegative").empty();
			$("#accuracy").empty();
			$("#threshold").empty();
			$("#captionFP").empty();
			$("#captionFN").empty();

			var testname = $(".show_test").val();
			//aggiungo tutte le immagini

			for(var j in result.tests)
			{
				if(result.tests[j].ID == testname)
				{
					$("#captionFP").html("<p class='right'>False<br>positives</p>");
					$("#captionFN").html("<p class='right'>False<br>negatives</p>");

					var objtest = result.tests[j];
					$("#accuracy").append("<p class='result'>"+ objtest.accuracy + "</p><p>accuracy</p>");
					$("#threshold").append("<p class='result'>"+ objtest.threshold + "</p><p>threshold</p>");

					var slidenumber = 1
					for(var i in objtest.falsepositive)
					{
						var x = document.createElement("IMG");
						var obj = objtest.falsepositive[i];
						x.setAttribute("src", obj);
						x.setAttribute("onclick","openModal();currentSlide("+ slidenumber +")");
						x.setAttribute("class","hover-shadow cursor");
						document.getElementById("falsepositive").appendChild(x);
						$('#modalcontent').append("<div class='mySlides'><div class='numbertext'>"+ slidenumber +" / 4</div><img src="+obj+" style='width:100%'></div>");
						slidenumber++;
					}
					for(var i in objtest.falsenegative)
					{
						var x = document.createElement("IMG");
						var obj = objtest.falsenegative[i];
						x.setAttribute("src", obj);
						x.setAttribute("onclick","openModal();currentSlide("+ slidenumber +")");
						x.setAttribute("class","hover-shadow cursor");
						document.getElementById("falsenegative").appendChild(x);
						$('#modalcontent').append("<div class='mySlides'><div class='numbertext'>"+ slidenumber +" / 4</div><img src="+obj+" style='width:100%'></div>");
						slidenumber++;
					}
				}

			}

			$('#modalcontent').append("<a class='prev' onclick='plusSlides(-1)'>&#10094;</a>");
			$('#modalcontent').append("<a class='next' onclick='plusSlides(1)'>&#10095;</a>");
			$('#modalcontent').append("<div class='caption-container'><p id='caption'></p></div>");

			//aggiungo tutte le caption
			slidenumber=1;
			for(var i in objtest.falsepositive)
			{
				var obj = objtest.falsepositive[i];
				$('#modalcontent').append("<div class='column-captions'><img class='demo cursor' src="+obj+" onclick='currentSlide("+ slidenumber +")'></div>");
				slidenumber++;
			}
			for(var i in objtest.falsenegative)
			{
				var obj = objtest.falsenegative[i];
				$('#modalcontent').append("<div class='column-captions'><img class='demo cursor' src="+obj+" onclick='currentSlide("+ slidenumber +")'></div>");
				slidenumber++;
			}
		}
	});
}

// Le seguenti funzioni sono a supporto della funzione addimages() ----------------
function openModal() {
	document.getElementById('myModal').style.display = "block";
}

function closeModal() {
	document.getElementById('myModal').style.display = "none";
}

var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
	showSlides(slideIndex += n);
}

function currentSlide(n) {
	showSlides(slideIndex = n);
}

function showSlides(n) {
	var i;
	var slides = document.getElementsByClassName("mySlides");
	var dots = document.getElementsByClassName("demo");
	var captionText = document.getElementById("caption");
	if (n > slides.length) {slideIndex = 1}
	if (n < 1) {slideIndex = slides.length}
	for (i = 0; i < slides.length; i++) {
		slides[i].style.display = "none";
	}
	for (i = 0; i < dots.length; i++) {
		dots[i].className = dots[i].className.replace(" active", "");
	}
	slides[slideIndex-1].style.display = "block";
	dots[slideIndex-1].className += " active";
	captionText.innerHTML = dots[slideIndex-1].alt;
}

//--------------------------------------------------------------------------

//TODO commentare
	// READ SIMULATION CONFIGURATION simulation
	function retrieveSimConfig(){

		selectArray = Array.prototype.map.call($(".moltiplicandum select"),(function(el){
			return el.value;
		}));

		JSON.stringify(selectArray);
		console.log("***  restrieveSimConfig  ********");
		console.log(selectArray);
		window.location = "show.html?arr="+selectArray;

		//document.getElementById("sim-buttons").style.display = "none"; // after clicking, hide buttons
		//document.getElementById("start").style.display = "block"; // after clicking, display watson logo

		/*
		//console.log(selectArray);
		document.getElementById("sim-buttons").style.display = "none"; // after clicking, hide buttons
		document.getElementById("start").style.display = "block"; // after clicking, display watson logo
		$('#start').html("<img src='ico/loading-indicator.gif' id='loading'>");
		*/
		// Send a http request with AJAX to retrieve contents from backend
		/*$.ajax(
		{
		url: '',
		type: 'POST',
		data:{ array: selectArray },
		dataType: 'json',
		success: function(result)
		{
		// faccio cose, vedo gente, schiaccio cinque
	}
});*/

}

	//TODO commentare
// GET DATA TO SHOW: questa dovrebbe essere la funzione che lega la richiesta dei test da mostrare in show.html???
function getDataShow(dataArray){

	//alert(dataArray);
	document.getElementById("start").style.display = "block";
	$('#start').html("<img src='ico/load.svg' id='loading'>");
	console.log("chiamata ajax GET")

	$.ajax(
		{
			url: 'https://visualrecognitiontester.eu-gb.mybluemix.net/RetrieveClassifiers',
			type: 'GET',
			data:{ array: dataArray },
			dataType: 'json',
			success: function(result)
			{
				console.log("SUCCESSOOOOOOOOOOO...MINGHIE!")
				Draw("json/frontend.json");
				//	Draw(result);
			}
		});

	}

//TODO commentare
	// POPULATE SIMULATION DROP DOWN MENUS
	function populateSelectSim(filename){

		$.ajax({														// load json file
			dataType: "json",
			url: filename,
			async: false,
			success: function(result)
			{

				// fill test set and cathegory drop down menu (only if status: ready)
				for(var i in result.testSets){
					var obj = result.testSets[i];
					if(obj.status == "ready"){
						$('.test_set').append($('<option>', {
							value: obj.ID,
							text: obj.label+" "+obj.size
						}));
						$('.avail_cat').append($('<option>', {
							value: obj.label,
							text: obj.label
						}));
					}
				}

				// fill classifier drop down menu (only if status: ready)
				for(var j in result.classifiers){
					var obj = result.classifiers[j];
					if(obj.status == "ready"){
						$('.avail_class').append($('<option>', {
							value: obj.ID,
							text: obj.label+" "+obj.trainingSize
						}));
					}
				}

				//update show page test set
				for(var j in result.tests){
					var obj = result.tests[j];
					$('.show_test').append($('<option>', {
						value: obj.ID,
						text: obj.ID
					}));
				}

			}
		});
	}

	/**
	 * @param IDelement: id dell'elemento html dove esporre la tabella
	 * @param table: array[row][col] che verrà esposta in formato tabella
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
					var txt = document.createTextNode(table[i][j]);
					var th = document.createElement('th');
					th.appendChild(txt);
					row.appendChild(th);
				}
				else{
					var td = document.createElement('td');
					var block = document.createElement('div');
					if(table[i][j]!=""){
					block.className = 'block';
					block.appendChild(document.createTextNode(table[i][j]));
					}else block.appendChild(document.createTextNode(table[i][j]));
					td.appendChild(block);
					row.appendChild(td);
					}

				
				/*if(i==0 || j==0){
					row.insertCell(-1).outerHTML = "<th>"+table[i][j]+"</th>";
				}
				else{
					row.insertCell(-1).outerHTML = "<td>"+table[i][j]+"</td>";
					}*/
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
					if(n_img.indexOf(obj.trainingsize) == -1) n_img.push(obj.trainingsize);
				}
				n_img.sort(function(a, b){return a - b;});
				
				//Inizializzazione matrice dei classificatori
				var sizeRow=label.length;
				var sizeCol=n_img.length;
				var matrix = new Array(sizeRow);
				for (var i = 0; i < sizeRow; i++) {
					matrix[i] = new Array(sizeCol);
					for(var j = 0; j < sizeCol; j++){
						matrix[i][j] = "";
					}
				}
				
				//Inizializzazione matrice da stampare (classificatori + label + cardinality)
				var print_table = new Array(sizeRow+1);
				for (var i = 0; i < (sizeRow+1); i++) {
					print_table[i] = new Array(sizeCol+1);
				}
				//Riempimento della matrice più interna
				for(var i in result)
				{
					var obj = result[i];
					var n = label.indexOf(obj.label); //row
					var m = n_img.indexOf(obj.trainingsize); //col
					
					if(matrix[n][m]=="") matrix[n][m]=obj.label+obj.trainingsize;
					//else matrix[n][m].push(obj.label+obj.trainingsize);
					
					/*
					if(obj.status == "training")
					{

						if(matrix[n][m] !=  "")
						matrix[n][m] = matrix[n][m].concat("<div class='blocktrain'><p>"+ obj.label + "-" + obj.trainingsize +"</p></div>");
						else matrix[n][m] = "<div class='blocktrain'><p>"+ obj.label + "-" + obj.trainingsize +"</p></div>";
					}
					else {
						if(matrix[n][m] !=  "")
						matrix[n][m] = matrix[n][m].concat("<div class='block'><p>"+ obj.label + "-" + obj.trainingsize + "</p></div>");
						else matrix[n][m] = "<div class='block'><p>" + obj.label + "-" + obj.trainingsize + "</p></div>";
					}*/
				}
				//inizializza header delle label
				print_table[0][0] = "Cardinality";
				for(var j = 0; j < sizeRow; j++)	print_table[j+1][0] = label[j];
				for(var i = 0; i < sizeCol; i++) print_table[0][i+1] = n_img[i];
				//imposta la matrice dei contenuti da stampare
				for(var i = 0; i < sizeRow; i++)
				for(var j = 0; j < sizeCol; j++)
				print_table[i+1][j+1] = matrix[i][j];
								
				//add a table (idelement and table to print)
				addTable("dvTable",print_table);
			}
		});

		//**************************************************************************
		//********* GET FROM THE SERVER TRAININGDATASETS ***************************
		//**************************************************************************
		$.ajax({
			contentType: "application/json",
	        dataType: "json",
			url: 'GetDataset',
			data: 'sub_type=training_set',
			async: false,
			success: function(result){
				var training_sets = new Array();
				training_sets.push(["Label", "Max number of images available"]);
				for(var i in result){
					var obj = result[i];
					training_sets.push([obj.label,obj.trainingSize]);
				}
				addTable("dvList",training_sets);
			}
		});

	}