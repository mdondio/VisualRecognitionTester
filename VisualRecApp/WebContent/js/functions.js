
/**
 * @returns save locally the result file depending on the checkboxes selected
 */
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
	$("input[type='submit']").attr("class", "submitmodal2");
	
}

/**
 * @param IDelement div id where append the table
 * @param table expected structure of classifier {_id/label/training_size/status} and first column of label
 * @returns build the table
 */
function addClassifierTable(IDelement,table){
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
				
				for(var k=0;k<(table[i][j]).length;k++)
				{
					var block = document.createElement('div');
					block.setAttribute("id",table[i][j][k]._id)
					
					block.addEventListener("click", function(){
						var IDstring = $(this).prop("id");
						swal({
							  title: 'Are you sure?',
							  text: 'You are deleting this classifier (ID: '+IDstring +"). You won't be able to revert this!",
							  type: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#3085d6',
							  cancelButtonColor: '#d33',
							  confirmButtonText: 'Yes, delete it!',
							  cancelButtonText: 'No, cancel!'
							}).then(function (isConfirm) {
							  
								if(isConfirm)
									{
									$.ajax({
										   		contentType : "application/json",
										   		dataType : "json",
										   		data : "classifierId=" + IDstring + "",
										   		url : 'DeleteClassifier',
										   		async : false,
										   		success : function(result) {
						   									swal('Deleted!','Classifier (ID: '+IDstring+') has been deleted.','success').then(function(){location.reload();})
							   								}
				   							});
									}
							})
					});

							block.setAttribute("class",'smoothrectangle '+table[i][j][k].status+'');
							block.setAttribute("data-tooltip","ID: "+table[i][j][k]._id+" status:"+table[i][j][k].status+" - label:"+table[i][j][k].label);
					
					block.appendChild(document.createTextNode(table[i][j][k].training_size+"_"+"v"+k));
					td.appendChild(block);
				}
				if((table[i][j]).length==0) td.appendChild(document.createTextNode(""));
				row.appendChild(td);
			}
		}
		tableElement.appendChild(row);
	}
	document.getElementById(IDelement).appendChild(tableElement);
}



/**
 * @param IDappend div element where append the descriptive rectangle
 * @param testname
 * @param label
 * @param classifier
 * @returns create and append a descriptive area of a testresult
 */
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


/**
 * @returns update all the fields of the result section (simulate.html - div id=showtest) starting from the JSON file stored in
 * the localStorage area (resultJSON and listJSON)
 */
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

/**
 * @returns update only the fields related to the single test of the result section (simulate.html - div id=showtest) starting from the JSON file stored in
 * the localStorage area (resultJSON and listJSON)
 */
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

			var listAUC = [];
			for(var i in result) {
				var obj = result[i];
				var singleObj = {}
				singleObj['x'] = obj.trainingSize;
				singleObj['AUC'] = obj.AUC;
				singleObj['Accuracy'] = obj.accuracyOpt;
				listAUC.push(singleObj);
			};

			//sort to better interpolate AUC curve
			listAUC.sort(function(a, b) {						
				return ((a.x < b.x) ? -1 : ((a.x == b.x) ? 0 : 1));
			});

			var xN = [];
			var yAUC = [];
			var yAccuracy = [];
			for(var i in listAUC)
			{
				xN.push(listAUC[i].x);
				yAUC.push(listAUC[i].AUC);
				yAccuracy.push(listAUC[i].Accuracy);
			}

			//ADD POINTS OF THE AUC CURVE
			var AUCcurves = {
				x: xN,
				y: yAUC,
				y: yAccuracy,
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
	for(var i=0;i<histogramNegative.length;i++)
		{
		negative.push(histogramNegative[i]);
		}
	
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


/**
 * @param object including all the information of a single test
 */
function setParameters(result) {
			$("#accuracy").empty();
			$("#threshold").empty();
			$("#accuracy").html(result.accuracyOpt.toFixed(2));
			$("#threshold").html(result.thresholdOpt.toFixed(2));
			$("#auctest").html(result.AUC.toFixed(2));
}

function openModal2() {
	populateListTestResult();
//	$('#savetestmodal').show("slow");
//	$('#savetestmodalbackground').show("slow");
//	$('#savetestmodalcontent').show("slow");
	$('#savetestmodal').fadeIn(100);
	$('#savetestmodalbackground').fadeIn(100);
	$('#savetestmodalcontent').fadeIn(100);
}

function closeModal2() {
//	$('#savetestmodal').hide("slow");
//	$('#savetestmodalbackground').hide("slow");
//	$('#savetestmodalcontent').hide("slow");
	$('#savetestmodal').fadeOut(100);
	$('#savetestmodalbackground').fadeOut(100);
	$('#savetestmodalcontent').fadeOut(100);
}

/*
 * =======================================================================
 * ============== FUNCTIONS FOR GALLERY MANAGEMENT =======================
 * =======================================================================
*/

var SLIDEINDEX = 1;
var GALLERY = "";

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
	console.log("id "+ $(this).attr("id"));
	console.log("class "+ $(this).attr("class"));
	console.log("name "+ $(this).attr("name"));
	currentSlide(slidenumber);
	}

function newImg(inputgallery,slidenumber){
	setGallery(inputgallery);
	openModal();
	currentSlide(slidenumber);
	}

function prevImg(inputgallery){
	setGallery(inputgallery);
	plusSlides(-1);
}

function nextImg(inputgallery){
	setGallery(inputgallery);
	plusSlides(1);
}

function openModal() {
	document.getElementById('myModal'+GALLERY).style.display = "block";
}

function closeModal() {
	document.getElementById('myModal'+GALLERY).style.display = "none";
}

function setGallery(galleryname){GALLERY=galleryname;};

showSlides(SLIDEINDEX);

function plusSlides(n) {
	showSlides(SLIDEINDEX += n);
}

function currentSlide(n) {
	showSlides(SLIDEINDEX = n);
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
			SLIDEINDEX = 1
		}
		if (n < 1) {
			SLIDEINDEX = slides.length
		}
		for (i = 0; i < slides.length; i++) {
			slides[i].style.display = "none";
		}
		for (i = 0; i < dots.length; i++) {
			dots[i].className = dots[i].className.replace(" active", "");
		}
		slides[SLIDEINDEX - 1].style.display = "block";
		dots[SLIDEINDEX - 1].className += " active";
		captionText.innerHTML = dots[SLIDEINDEX - 1].alt;
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
/*
 * ==============================================================================
 * ============== END OF FUNCTIONS FOR GALLERY MANAGEMENT =======================
 * ==============================================================================
*/




/*
 * ==============================================================================
 * ========================= AJAX CALLS =================================
 * ==============================================================================
*/

/**
 * @returns set-up JSON file with simulate.html inputs, calls GetTestResult servlet and print results
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

				var testdetails = localStorage.getItem("listJSON");

				$.ajax(
						{
							url: 'GetTestResult',
							type: 'GET',
							data:{ array: testdetails },
							dataType: 'json',
							async: true,
							success: function(result)
							{
								$("#waiting").fadeOut(1000);
								$("#showtest").fadeIn(2000);
								localStorage.setItem("resultJSON", JSON.stringify(result));
								printShowPage();
							}
						});
	}

/*
 * @param dataset_type (test_set,training_set,all)
 * @param IDselector selector of the select object (or objects)
 * @returns
 */
function buildSelectDataSet(dataset_type,IDselector){

	$.ajax({													
		dataType: "json",
		url: 'GetDataset',
		async: true,
		success: function(result)
		{
			for(var i in result){
				var obj = result[i];
				
	if((obj.images.positive.length + obj.images.negative.length)*(dataset_type=="test_set")<250)
		{
					$(IDselector).append($('<option>', {
						value: obj._id,
						text: obj._id
					}));
		}
			}
		}
	});
}

/**
 * @param status (ready,training,zombie)
 * @param IDselector
 * @returns build a select element with the classifier of the status selected
 */
function buildSelectClassifier(status,IDselector){
	
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		url: 'GetClassifier',
		async: true,
		success: function(result)
		{
			for(var j in result){
				var obj = result[j];
				if(obj.status == status){
					$(IDselector).append($('<option>', {
						value: obj._id,
						text: obj._id+" ("+obj.training_size+")"
					}));
				}
			}
		}
	});
	
}

/**
 * @returns Build the dashboard page
 * @help servlet used: GetInstance, GetClassifier, GetDataset
 */
function generateHome(){

	//**************************************************************************
	//********* GET FROM THE SERVER VRINSTANCES ********************************
	//**************************************************************************
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		url: 'GetInstance',
		async: true,
		success: function(result){
			var free = 0;
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
		url: 'GetClassifier',
		async: true,
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
			$('#zombieclass').html(zombie);
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
					matrix[i][j] = [];
				}
			}

			//Inizializzazione matrice da stampare (classificatori + label + cardinality)
			var print_table = new Array(sizeRow);
			for (var i = 0; i < sizeRow; i++) {
				print_table[i] = new Array(sizeCol+1);
				for(var j=0;j<(sizeCol+1);j++) print_table[i][j] = [];
			}
			//Riempimento della matrice più interna
			for(var i in result)
			{
				var obj = result[i];
				var n = label.indexOf(obj.label); //row
				var m = n_img.indexOf(obj.training_size); //col
				var k = (matrix[n][m]).push(obj);
			}
			//inizializza header delle label
			for(var i = 0; i < sizeRow; i++)	(print_table[i][0]).push(label[i]);
			//imposta la matrice dei contenuti da stampare
			for(var i = 0; i < sizeRow; i++)
				for(var j = 0; j < sizeCol; j++)
					for(var k = 0; k < (matrix[i][j]).length; k++)
						print_table[i][j+1][k] = matrix[i][j][k];

			//add a table (idelement and table to print)
			addClassifierTable("dvTable",print_table);
		}
	});

	//**************************************************************************
	//********* GET FROM THE SERVER DATASETS ***************************
	//**************************************************************************
	$.ajax({
		contentType : "application/json",
		dataType : "json",
		url : 'GetDataset',
		async : true,
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
}

/**
 * @returns call the SubmitTrainJob servlet to start training of e new classifier (a datasetid and a label must be already selected)
 */
function startTrain(){
	
	var datasetId = $("#labelselected").val();
	var label = $("#labelselected").html();
	if(datasetId & label)
		{
 	$.ajax({
		contentType : "application/json",
		dataType : "json",
		data : "datasetId=" + datasetId + "&label="+label,
		url : 'SubmitTrainJob',
		async : true,
		success : function(result) {
			swal('Trained!',
					'Your classifier has been trained!',
					'success').then(function(){window.location.href="home.html"})
		}
	});
		}else{
			swal({
				title: 'Warning',
				text: 'You have to select a valid dataset of images!',
				type: 'warning',});
		} 	
}

/*
 * ==============================================================================
 * ========================= END OF AJAX CALLS =================================
 * ==============================================================================
*/
