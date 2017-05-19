
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
    	
    	if(objtest.name==checkedValue[count]){
    		
    		directJSON.push(obj); 
    		count++;
    		
    	}
    	
    }
    
	 var json = JSON.stringify(directJSON);
	 var blob = new Blob([json], {type: "application/json"});
	  
	 closeModal2();
    
	 
	 if( count!= 0 ){
		 
	     swal({
			  title: "Insert File Name",
			  text: "Give JSON file a name:",
			  type: "info",
			  input: "text",
			  confirmButtonColor: '#5cb85c',
			  confirmButtonText: 'Save!',
			  showCancelButton: true,
			  allowOutsideClick: false,
			  allowEscapeKey: true,
			  inputPlaceholder: "...filename..."
			}).then( function (result) {
				
				if( result != null && result != "" )
					saveAs(blob, result + ".json");
				
				else{
					
					swal({
						  title: 'Error!',
						  text: 'For correctly saving a JSON file you have to insert a file name!',
						  type: 'error',
						  confirmButtonColor: '#f0ad4e',
						  confirmButtonText: 'Ok'
						})
					
				}
					
	
			})

	 }else{
		 
		 swal({
			  title: 'Error!',
			  text: 'For correctly saving a JSON file you have to select at least a result!',
			  type: 'error',
			  confirmButtonColor: '#f0ad4e',
			  confirmButtonText: 'Ok'
			})
		 
	 }
			
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
							  title: 'ID: "+IDstring"',
							  text: "\n\n<input type='submit' class='submitmodal2' value='Edit'>",
							  type: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#3085d6',
							  cancelButtonColor: '#d33',
							  confirmButtonText: 'Delete!',
							  cancelButtonText: 'Cancel'
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
					
//					block.appendChild(document.createTextNode(table[i][j][k].training_size+"_"+"v"+k));
					block.appendChild(document.createTextNode(table[i][j][k].shortname));
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

var numberBlock = 0;

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
	icon1.setAttribute("number", numberBlock);
	icon1.setAttribute("id","garbage"+testname);
	
	numberBlock++;
	
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
 * @returns build the select menu base on good testresults object
 */
function buildSelectTestResult(IDselector) {
	
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
				imageUrl : "img/tired2.png",
				imageWidth: 240,
				imageHeight: 200,
				text : "Classifier "
					+ testdetails[testcount].classifier
					+ " is exhausted. Wait 24h and you will regain your free API calls",
			});
		} else {
			$(IDselector).append($('<option>', {
				value : testdetails[testcount].name,
				text : testdetails[testcount].name
			}));
		}
		testcount++;
	}
	
}

/**
 * @returns update only the fields related to the single test of the result section (simulate.html - div id=showtest) starting from the JSON file stored in
 * the localStorage area (resultJSON and listJSON)
 */
function updateTestFields(IDselector) {
	
	//carica i file da local storage
	var result = JSON.parse(localStorage.getItem("resultJSON"));
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));

	//disegna gli oggetti dipendenti dal test selezionato
	var testname = $(IDselector).val();
	console.log(testname)
	drawRocCurves(testname);
	drawIndexes(testname);
	
	for ( var j in testdetails) {
		if (testdetails[j].name == testname) {
			setParameters(result[j]);
			
			//var positive_images = [];
//            positive_images = [];
//            for (k = 0; k < result[i].images.positive.length; k++)
//                positive_images.push("GetImage?image_id=" + result[i].images.positive[k]);
			
			var negative_images = [];
			for(var i=0;i<result[j].falseNegativeOpt.length;i++) 
				negative_images.push("GetImage?image_id="+result[j].falseNegativeOpt[i]);
			$("#galleryFN").empty()
			createGallery('galleryFN',negative_images,"showtestNEG");
			
			var positive_images = [];
			for(var i=0;i<result[j].falsePositiveOpt.length;i++) 
				positive_images.push("GetImage?image_id="+result[j].falsePositiveOpt[i]);
			$("#galleryFP").empty()
			createGallery('galleryFP',positive_images,"showtestPOS");
			
			DrawHistogram(result[j].histogramNegative,result[j].histogramPositive);
		}
	}
}

function drawIndexes(testname){
	
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

	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
	var result = JSON.parse(localStorage.getItem("resultJSON"));

	var listIndexes = [];
	for(var i in result) {
		var obj = result[i];
		var singleObj = {}
		singleObj['x'] = obj.trainingSize;
		singleObj['AUC'] = obj.AUC;
		singleObj['Accuracy'] = obj.accuracyOpt;
		singleObj['th'] = obj.thresholdOpt;
		listIndexes.push(singleObj);
	};

	//sort to better interpolate AUC curve
	listIndexes.sort(function(a, b) {						
		return ((a.x < b.x) ? -1 : ((a.x == b.x) ? 0 : 1));
	});

	var xN = [];
	var yAUC = [];
	var yAccuracy = [];
	var yTh = [];
	for(var i in listIndexes)
	{
		xN.push(listIndexes[i].x);
		yAUC.push(listIndexes[i].AUC);
		yAccuracy.push(listIndexes[i].Accuracy);
		yTh.push(listIndexes[i].th);
	}

	//ADD POINTS OF THE AUC CURVE
	var Indexes = [];
	
	for(var i=0;i<xN.length;i++){

		var objJSON = testdetails[i];
		
			if (objJSON.name == testname) {

				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yAUC[i]],
					mode: "markers",
					name: "AUC "+objJSON.name,
					marker: {
						"size": 12,
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
				
				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yAccuracy[i]],
					mode: "markers",
					name: "Accuracy "+objJSON.name,
					marker: {
						"size": 12,
						"symbol": "triangle-up",
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
				
				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yTh[i]],
					mode: "markers",
					name: "Threshold opt "+objJSON.name,
					marker: {
						"size": 12,
						"symbol": "square",
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
		
			}else{
				
				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yAUC[i]],
					opacity: 0.4,
					mode: "markers",
					name: "AUC "+objJSON.name,
					marker: {
						"size": 8,
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
				
				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yAccuracy[i]],
					opacity: 0.4,
					mode: "markers",
					name: "Accuracy "+objJSON.name,
					marker: {
						"size": 8,
						"symbol": "triangle-up",
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
				
				Indexes.push({
					type: "scatter",
					x: [xN[i]],
					y: [yTh[i]],
					mode: "markers",
					opacity: 0.4,
					name: "Threshold opt "+objJSON.name,
					marker: {
						"size": 8,
						"symbol": "square",
						"color": "rgb("+colorpalette[i][0]+","+colorpalette[i][1]+","+colorpalette[i][2]+")"
					}
				});
				
			}

	}
	
	var layout = {
			showlegend: false,
			legend: {
				x: 0.1,
				y: -1,
				height: 20,
			},
			xaxis: {
				title: 'N',
				range: [xN[0],xN[xN.length-1]],
				autorange: true
			},
			yaxis: {
				title: 'Indexes',
				range: [0,1.1],
				autorange: false
			},
			autosize: false,
			  width: 450,
			  height: 550,
			  margin: {
			    l: 70,
			    r: 50,
			    b: 80,
			    t: 30,
			    pad: 8
			  },
			  paper_bgcolor: '#f2f2f2',
			  plot_bgcolor: '#f2f2f2'
	};

	Plotly.newPlot('graph2',Indexes,layout);
	
}

function drawRocCurves(testname){
	
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
	var result = JSON.parse(localStorage.getItem("resultJSON"));
//	var testname = $("#show_test").val();

	for(var i in result){
		var obj = result[i];
		var x = [];
		var y = [];
		for(var j in obj.fprTrace) x.push(obj.fprTrace[j]);
		for(var j in obj.tprTrace) y.push(obj.tprTrace[j]);
		var objJSON = testdetails[count];
		
		
			if (testdetails[count].name == testname) {

		ROCcurves.push(
				{
					type: "scatter",
					x: x,
					y: y,
					mode: "lines",
					name: objJSON.name,
					line: {
						"width": 3,
//						"shape": "spline",
						"color": "rgb("+colorpalette[count][0]+","+colorpalette[count][1]+","+colorpalette[count][2]+")"
					},
				}
		);
		
			}else{
				
				ROCcurves.push(
						{
							type: "scatter",
							opacity: 0.3,
							x: x,
							y: y,
							mode: "lines",
							name: objJSON.name,
							line: {
								"shape": "spline",
								"color": "rgb("+colorpalette[count][0]+","+colorpalette[count][1]+","+colorpalette[count][2]+")"
							},
							marker:{
								"color": "rgb("+colorpalette[count][0]+","+colorpalette[count][1]+","+colorpalette[count][2]+")"
							}
						}
				);
				
			}
		
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

	//LAYOUT GRAFICO ROC
	var layout = {
			legend: {
				x: 0.2,
				y: -0.6,
			},
			xaxis: {
				title: 'fpr',
				range: [0 , 1],
//				autorange: true
			},
			yaxis: {
				title: 'tpr',
				range: [0 , 1.1],
//				autorange: true
			},
			autosize: false,
			  width: 450,
			  height: 550,
			  margin: {
			    l: 70,
			    r: 50,
			    b: 0,
			    t: 30,
			    pad: 0
			  },
			  paper_bgcolor: '#f2f2f2',
			  plot_bgcolor: '#f2f2f2'
	};

	Plotly.newPlot('graph1',ROCcurves,layout);
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
			"color": "rgb(130,127,178)",
		},
	xbins:{
		start: 0,
		end: 1,
		size: 0.02		
	}
	};
	
	//ADD positive histogram
	var positiveTrace = {
		x: positive,
		type: "histogram",
		opacity: 0.6,
		name: "Positive distribution",
		marker: {
			"color": "rgb(0,166,160)",
		},
		xbins:{
			start: 0,
			end: 1,
			size: 0.02		
		}
	};
	
	var data = [negativeTrace , positiveTrace];

		var layout = {
//		showlegend : true,
		legend : {
			x : 0.2,
			y : -0.6,
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
			range : [0,50],
//			autorange : true
		},
		  autosize: false,
		  width: 450,
		  height: 550,
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

			//var img_path = "GetImage?image_id=";
			var img_path = "";
			setGallery(inputgallery);
			
			$("#modalcontent" + GALLERY).empty();
			$("#gallery" + GALLERY).empty();

			var slidenumber = 1;
			var totalslide = result.length;
			
			var simulate_lazy = false;

			//Aggiungo le immagini nella modalità preview (elemento div con id gallery)
			for ( var i in result) {
				
				var x = document.createElement("IMG");
				
				//Handling simulate results images
				if( inputgallery.includes("show") ){
					x.setAttribute("data-src", img_path + result[i]);
					simulate_lazy = true;
				}
				else
					x.setAttribute("data-src", img_path + result[i]);
				
				x.setAttribute("onclick", 'openModal();currentSlide('+slidenumber+')');
				// TODO approfondire addEventListener anche per pezzo successivo
				x.addEventListener("click", function(event) {
					newImg(inputgallery, slidenumber);
					event.preventDefault();					
				});
				x.setAttribute("class", "test hover-shadow cursor"); //was test hover-shadow cursor
				x.setAttribute("onclick", 'currentSlide('+slidenumber+')');
				document.getElementById("gallery" + GALLERY).appendChild(x);
				
				//For GIAN!
				var gian_str = result[i];
				gian_str = gian_str.substring(gian_str.indexOf("=") + 1);
				//For GIAN! {END}
				
				$('#modalcontent' + GALLERY).append(
						"<div class='mySlides" + GALLERY
								+ "'><div class='numbertext'>" + slidenumber
								+ " / " + totalslide
//								+ "</div><h1>"+gian_str+"</h1><img class='modal-img' src="
//								+ img_path + result[i] + "></div>");
								+ "</div><h1>"+gian_str+"</h1>><img class='modal-img' data-src="
								+ img_path + result[i] + "></div>");
				
								//+ img_path + result[i] + " src="+ img_path + result[i] + " ></div>");
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
			
				// click sulle frecce
				a_prev.addEventListener("click", function(event) {
					prevImg(inputgallery);
					event.preventDefault();
				});
				a_next.addEventListener("click", function(event) {
					nextImg(inputgallery);
					event.preventDefault();
				});
				
				// pressione tasti freccia della tastiera
//				$(this).keydown(function(event) {
//				  if(event.keyCode == 37) { // left
//					  prevImg(inputgallery);
//					  event.preventDefault();
//					  console.log("left pressed!");
//				  }
//				  else if(event.keyCode == 39) { // right
//					  nextImg(inputgallery);
//					  event.preventDefault();
//					  console.log("right pressed!");
//				  }
//				});	
			
			$('#modalcontent' + GALLERY).append(a_prev);
			$('#modalcontent' + GALLERY).append(a_next);
			
			//Aggiungo l'elemento div che conterrà la galleria di immagini da mostrare sotto l'immagine in ZOOM (elemento div con modalcontent)
			$('#modalcontent' + GALLERY).append("<div class='caption-container'><p id='caption" + GALLERY + "'></p></div>");
			slidenumber = 1;
			for ( var i in result) {
				var obj = result[i];

				var x = document.createElement("IMG");
				x.setAttribute("data-src", img_path + result[i]);
//				x.setAttribute("src", img_path + result[i]);
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
			
			if( simulate_lazy )
				testingLoading();
				
}

function newImgZoom(inputgallery,slidenumber){
	setGallery(inputgallery);
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
//		console.log("HERE --> " + GALLERY);
		var changeForLazy = document.getElementsByClassName("modal-img");
		var dots = document.getElementsByClassName("demo" + GALLERY);
		var captionText = document.getElementById("caption" + GALLERY);
		if (n > slides.length) {
			SLIDEINDEX = 1
		}
		if (n < 1) {
			SLIDEINDEX = slides.length
		}
		
		for( var i = 0; i < changeForLazy.length; i++ ){
			
			var old = changeForLazy[i].getAttribute("data-src");
			changeForLazy[i].src = old;
			
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

function testingLoading(){
	
	$('.test').lazy({
        enableThrottle: true,
        throttle: 250
    });
	
}

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
								
								buildSelectTestResult('#show_test');
								updateTestFields('#show_test');

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
		data: "_id=",
		async: true,
		success: function(result){
			
			for(var i in result){
				var obj = result[i];
				
				if((obj.images.positive.length + obj.images.negative.length)*(dataset_type=="test_set")<250){
						
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
		data: "_id=",
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
		data : "_id=",
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
			setListHeight($('#dvTable').height());
		}
	});

	//**************************************************************************
	//********* GET FROM THE SERVER DATASETS ***************************
	//**************************************************************************
	$.ajax({
		contentType : "application/json",
		dataType : "json",
		url : 'GetDataset',
		data : "_id=",
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
	var description = $("#classifierdesc").val();
	var shortname = $("#classifiershortname").val(); //CONTROLLO SHORTNAME UNIVOCO
	
	console.log(datasetId);
	console.log(label);
	console.log(description);
	console.log(shortname);
	
	if(datasetId && label)
		{
 	$.ajax({
		contentType : "application/json",
		dataType : "json",
		data : "datasetId=" + datasetId + "&label="+label + "&description="+description + "&shortname="+shortname,
		url : 'SubmitTrainJob',
		async : true,
		success : function(result) {
			if(result.hasOwnProperty('error'))
				{
				swal({
					title: 'Warning',
					text: result.error,
					type: 'warning',});
				}
			else{
			swal('Launched!',
					'Your classifier training has been launched!',
					'success').then(function(){window.location.href="home.html"})
		}
			}
	});
		}else{
			swal({
				title: 'Warning',
				text: 'You have to select a valid dataset of images!',
				type: 'warning',});
		} 	
}

/**		
* Function called in generateHome to set the height of the dataset list equal to the height of the classifier table		
*/		
function setListHeight(height){		
	console.log(height);		
	$('#listdataset').css("height", height);		
		
}

/*
 * ==============================================================================
 * ========================= END OF AJAX CALLS =================================
 * ==============================================================================
*/


/*
 * ==============================================================================
 * ========================= UPLOAD FUNCTIONS ===================================
 * ==============================================================================
*/



/*
 * ==============================================================================
 * ========================= UPLOAD FUNCTIONS {END} =============================
 * ==============================================================================
*/