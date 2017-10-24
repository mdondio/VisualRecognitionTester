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
			  html: "Give JSON file a name:<br><br>",
			  type: "info",
			  input: "text",
			  buttonsStyling: false,
			  customClass: 'modal-container',
			  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
			  confirmButtonText: 'Save file',
			  cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
			  cancelButtonText: 'Cancel',
			  showCancelButton: true,
			  allowOutsideClick: false,
			  allowEscapeKey: true,
			  inputPlaceholder: "Hint filename here..."
			}).then( function (result) {
				
				if( result != null && result != "" )
					saveAs(blob, result + ".json");
				
				else{
					
					swal({
						  title: 'Error!',
						  html: 'For correctly saving a JSON file you have to insert a file name!<br><br>',
						  type: 'error',
						  buttonsStyling: false,
						  showCancelButton: false,
						  customClass: 'modal-container',
						  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
						  confirmButtonText: 'Got it'
						})
					
				}
					
	
			})

	 }else{
		 
		 swal({
			  title: 'Error!',
			  html: 'For correctly saving a JSON file you have to select at least one result!<br><br>',
			  type: 'error',
			  buttonsStyling: false,
			  showCancelButton: false,
			  customClass: 'modal-container',
			  cancelButtonClass: 'bx--btn bx--btn--primary margin-lr',
			  confirmButtonText: 'Got it'
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
	
	//flag for disabling swal buttons in case of a training classifier
	 var flag = true;
	
	
	//Add the data rows
	for (var i = 0; i < rowCount; i++) {
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
						
						var classifierClass = $(this).prop("class");
						
						var status;
						 						
						if( classifierClass.includes("ready") ){
							
							flag = false; 
							
						} else {
							
							if(classifierClass.includes("training"))
								status = "training";
							else
								status = "zombie";
							
						}
						
						
						setClassID(IDstring);
												
						var IDshortname = returnClassifierDetail(IDstring, "shortname");
						var IDdescription = returnClassifierDetail(IDstring, "description");
						
						if (flag){
							
							swal({
								title: IDshortname,
								html: 'Your classifier is ' + status +'!<br><br>',
								buttonsStyling: false,
								customClass: 'modal-container',
								showCancelButton: false,
								confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
								confirmButtonText: 'Ok'
							})
							
						} else {
						
						swal({
							  title: IDshortname,
							  html: ""+IDdescription+"<br><br><button type='submit' id='editButton' class='bx--btn bx--btn--primary margin-lr'>Edit classifier</button>",
							  showCancelButton: true,
							  buttonsStyling: false,
							  customClass: 'modal-container',
							  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
							  cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
							  confirmButtonText: 'Delete it!',
							  cancelButtonText: 'Cancel'
							}).then(function (isConfirm) {
							  
								if(isConfirm){
									
									swal({
										  title: "Are you sure?",
										  html: "Are you really sure you want to delete this classifier?<br><br>",
										  type: "warning",
										  buttonsStyling: false,
										  customClass: 'modal-container',
										  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
										  cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
										  confirmButtonText: 'Delete it!',
										  cancelButtonText: 'Cancel',
										  showCancelButton: true,
										  allowOutsideClick: false,
										  allowEscapeKey: true
										}).then( function(result) {
											
											//User really wants to delete the classifier
											if(result){
												
												$.ajax({

													   	contentType : "application/json",
													   	dataType : "json",
													  	data : "classifierId=" + IDstring + "",
													  	url : 'DeleteClassifier',
													   	async : false,
													   	success : function(result) {
													   											   						
									   						swal({
															  title: "Deleted!",
															  html: "Classifier " + IDshortname + " (ID: "+IDstring+") has been deleted.<br><br>",
															  type: "success",
															  buttonsStyling: false,
															  customClass: 'modal-container',
															  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
															  confirmButtonText: 'Yeah!',
															  showCancelButton: false,
															  allowOutsideClick: false,
															  allowEscapeKey: true
															}).then( function(result) { location.reload(); });
										   				
													   	}
												
							   					});
												
											} //User really wants to delete the classifier {END}
												
										});
									
									}
							})
							
						}
							
					});

					block.setAttribute("class",'bx--card minimal '+table[i][j][k].status+'');
					block.setAttribute("data-tooltip","ID: "+table[i][j][k]._id+" status: "+table[i][j][k].status+" - label: "+table[i][j][k].label);
												
					block.appendChild(document.createTextNode(table[i][j][k].shortname+" "+table[i][j][k].training_size));
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

function deleteClassifierFromDetailPage(classID, shortName){
	
	swal({
		  title: "Are you sure?",
		  html: "Are you really sure you want to delete this classifier?<br><br>",
		  type: "warning",
		  customClass: 'modal-container',
		  buttonsStyling: false,
		  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
		  cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
		  confirmButtonText: 'Yes, delete it!',
		  cancelButtonText: 'No, cancel',
		  showCancelButton: true,
		  allowOutsideClick: false,
		  allowEscapeKey: true
		}).then(function (isConfirm) {
		  
			if(isConfirm){
				
				$.ajax({

				   	contentType : "application/json",
				   	dataType : "json",
				  	data : "classifierId=" + classID,
				  	url : 'DeleteClassifier',
				   	async : false,
				   	success : function(result) {
				   		   						
						swal({
   							title: 'Deleted!',
   							html: 'Classifier ' + shortName + ' (ID: '+classID+') has been deleted.<br><br>',
   							type: 'success',
   							buttonsStyling: false,
   							customClass: 'modal-container',
							confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
							confirmButtonText: 'Yeah!',
							showCancelButton: false,
							allowOutsideClick: false,
							allowEscapeKey: false
   						}).then(function(){window.location.href = 'home.html';})   						
	   				
				   	}
			
				});
			}
				
							
		});
	
}

function deleteDataset(e, datasetID){
	
	swal({
        title: 'Are you sure?',
        html: 'Are you sure you want to delete dataset<br><br> [' + datasetID + ']?<br><br>',
        type: 'warning',
        buttonsStyling: false,
        customClass: 'modal-container',
        showCancelButton: true,
        confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
        cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
        confirmButtonText: 'Yes, delete it',
        cancelButtonText: 'No, cancel'
    }).then(function(isConfirm) {
        if (isConfirm)
            handlerDeletion(e, datasetID);
        else {
            swal({
                title: 'Operation Aborted',
                html: 'This dataset is safe :)<br><br>',
                type: 'error',
                buttonsStyling: false,
                showCancelButton: false,
                customClass: 'modal-container',
                confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
                confirmButtonText: 'Yeah!',
            })
        }
    })

}

function handlerDeletion(e, datasetID){
	
	e.preventDefault();
    var data = new FormData();
    data.append('type', 'delete');
    data.append('datasetId', datasetID);
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'SubmitDatasetJob', true);
    //Operation communication progress
    xhr.onload = function(e) {
            //Everything seems to be OK
            if (this.status == 200) {
                swal({
                    title: 'Succes!',
                    html: 'Dataset [' + idgallery + '] has been correctly deleted!<br><br>',
                    type: "success",
                    showCancelButton: false,
                    buttonsStyling: false,
                    customClass: 'modal-container',
                    confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
                    confirmButtonText: 'Yeah!'
                }).then(function(isConfirm) {
                    if (isConfirm)
                        window.location.href = 'home.html';
                })
            } else {
                var err_type = this.status;
                var alert_title, alert_text, alert_img = null;
                switch (err_type) {
                    //BAD (cit. the Donald) request
                    case 400:
                        alert_title = "BAD REQUEST";
                        alert_text = "Our server could not understand the request...\nSorry about that.\n\n";
                        break;
                    case 401:
                        alert_title = "UNAUTHORIZED";
                        alert_text = "Authentication is needed to get requested response.\n\n";
                        break;
                    case 404:
                        alert_title = "NOT FOUND";
                        alert_text = "Our Server wasn't able to retrieve the requested resource. \n\nI know right?!\n";
                        break;
                    case 408:
                        alert_title = "REQUEST TIMEOUT";
                        alert_text = "Our server did not receive a complete request message within the time that it was prepared to wait.\nHe's impatient...\n\n";
                        break;
                        //This is serious
                    case 500:
                        alert_title = "INTERNAL ERROR";
                        alert_text = "This is on our side...sorry about that!\n\n";
                        alert_img = 'images/internal_error.jpg';
                        break;
                    case 503:
                        alert_title = "SERVICE'S OVERLOADED";
                        alert_text = "Our server is really busy at the moment, try again in a few minutes...\n\n";
                        break;
                    default:
                        alert_title = "GENERIC ERROR";
                        alert_text = "An error occured...try again please!\n";
                }
                swal({
                    title: '*** ' + alert_title + ' ***',
                    html: alert_text + '<br><br>',
                    type: 'error',
                    imageUrl: alert_img,
                    imageWidth: 800,
                    imageHeight: 200,
                    showCancelButton: false,
                    customClass: 'modal-container',
                    confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
                    confirmButtonText: 'Ok :('
                }).then(function(isConfirm) {
                    if (isConfirm)
                        location.reload();
                })
            }
        } //Operation communication progress END
    
    xhr.send(data);
	
}

var classIDFin;

function setClassID(classID){
	
	classIDFin = classID;
	
}

function getClassID(){
	
	return classIDFin;
	
}

/**
 * @param IDappend div element where append the descriptive rectangle
 * @param testname
 * @param label
 * @param classifier
 * @returns create and append a descriptive area of a testresult
 */

var numberBlock = 0;

function createBlockTest(IDappend,testname,description,status){
	
	var block = document.createElement("div");
	block.setAttribute("class", "bx--card mylarge");
	
//	======================
	var blocktest = document.createElement("div");
	blocktest.setAttribute("class", "cardattribute");
	blocktest.setAttribute("style", "float:left;");
	
	var pblockT1 = document.createElement("p");
	pblockT1.setAttribute("class", "bx--label");
	pblockT1.appendChild(document.createTextNode(testname));
	
	var pblockP1 = document.createElement("p");
	pblockP1.appendChild(document.createTextNode("Description: "+description));
	
	var pblockP2 = document.createElement("p");
	pblockP2.appendChild(document.createTextNode("Status: "+status));
	
	blocktest.appendChild(pblockT1);
	blocktest.appendChild(pblockP1);
	blocktest.appendChild(pblockP2);
//	===========================
	
	var blockimg = document.createElement("div");
	blockimg.setAttribute("class", "cardattribute");

	
	var icon1 = document.createElement("img");
	icon1.setAttribute("class", "mycard-icon");
	icon1.setAttribute("src", "ico/garbageDARK.png");
	icon1.setAttribute("number", numberBlock);
	icon1.setAttribute("id", "garbage"+testname);
	icon1.setAttribute("style", "float: right;width:10%;");
	
	blockimg.appendChild(icon1);
	
	numberBlock++;
	
	block.appendChild(blocktest);
	block.appendChild(blockimg);
	
	$("#"+IDappend+"").append(block);
}


/**
 * flag to hide tests in case of errors
 */
var hideresult = false;


/**
 * @returns build the select menu base on good testresults object
 *  -1: history view
 *   0: add successful test
 *   1: add error test
 *   2: add zombie test
 */
function buildSelectTestResult(IDselector, index) {
	
	//carica i file da local storage
	var result = JSON.parse(localStorage.getItem("resultJSON"));
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
	var iteration = result.length-1;
	console.log("*** THIS IS result *** : " + JSON.stringify(result) )
	console.log("*** THIS IS testdetails *** : " + JSON.stringify(testdetails) )

	switch (index) {
	case -1:
		var testcount = 0;
		for ( var j in result) {
			var obj = result[j];
				$(IDselector).append($('<option>', {
					value : result[testcount].name,
					text : result[testcount].name +" (testset-trainingset) "+ result[testcount].ID
				}));
			testcount++;
		}
		break;
	case 0:
		$(IDselector).append($('<option>', {
			value : result[iteration].name,
			text : result[iteration].name +" (testset-trainingset) "+ result[testcount].ID,
			id : result[iteration].name
		}));
		popNotification(result[iteration].name, "Test was successful!", "Results displayed.", "success");
		$(IDselector).data('key', iteration);
		hideresult = false;
		$('#selecttest-gray-roc').css("display", "none");
		break;
	case 1:
		$(IDselector).append($('<option>', {
			value : result[iteration].name,
			text : result[iteration].name +" (testset-trainingset) "+ result[testcount].ID,
			id : result[iteration].name
		}));
		$('#'+result[iteration].name).attr("disabled", "disabled");
		popNotification(result[iteration].name, "Some problems occurred: test results not displayed.", "Please check your test!", "error");
		hideresult = true;
		break;
	case 2:
		$(IDselector).append($('<option>', {
			value : "ZOMBIE, call: "+iteration,
			text : "ZOMBIE, call: "+iteration,
			id : "ZOMBIE, call: "+iteration
		}));
		$('#'+"ZOMBIE, call: "+iteration).attr("disabled", "disabled");
		popNotification("ZOMBIE, call: "+iteration, "Classifier went zombie: test results not displayed.", "API calls limit reached.", "warning");
		hideresult = true;
		break;
	default:
		console.log("**** [buildSelectTestResult] WARNING: beahviour not handled!!!!");
	} 
	
	// Handling exploring old test(s)
//	if( index == -1 ){
//		
//		var testcount = 0;
//		for ( var j in result) {
//			var obj = result[j];
//				$(IDselector).append($('<option>', {
//					value : testdetails[testcount].name,
//					text : testdetails[testcount].name
//				}));
//			testcount++;
//		}
//		
//	}
//	else{
//	console.log("****** INDEX *****"+index)
		//Manage notifications and filter population in simulate page
	
	//SUCCESS
//		if(result[index].notification == "success"){
//			$(IDselector).append($('<option>', {
//				value : testdetails[index].name,
//				text : testdetails[index].name,
//				id : testdetails[index].name
//			}));
//			popNotification(testdetails[index].name, "Test was successful!", "Results displayed.", "success");
//			$(IDselector).data('key', index);
//			hideresult = false;
//			$('#selecttest-gray-roc').css("display", "none");
//		}
	
		
	//GENERAL error
//		else if(result[index].notification == "error"){
//			$(IDselector).append($('<option>', {
//				value : testdetails[index].name,
//				text : testdetails[index].name,
//				id : testdetails[index].name
//			}));
//			$('#'+testdetails[index].name).attr("disabled", "disabled");
//			popNotification(testdetails[index].name, "Some problems occurred: test results not displayed.", "Please check your test!", "error");
//			hideresult = true;
//		}
		
	//ZOMBIE classifier
//		else if(result[index].ID == null){
//			$(IDselector).append($('<option>', {
//				value : testdetails[index].name,
//				text : testdetails[index].name,
//				id : testdetails[index].name
//			}));
//			$('#'+testdetails[index].name).attr("disabled", "disabled");
//			popNotification(testdetails[index].name, "Classifier went zombie: test results not displayed.", "API calls limit reached.", "warning");
//			hideresult = true;
//		}
	
		
//	}
	
}


function popNotification(title, subtitle, caption, type){
	
	var number = $('.bx--toast-notification:visible').length; 
	var notification = '<div id="notification-'+title+'" style="display:flex" data-notification class="notification-'+type+' bx--toast-notification bx--toast-notification--'+type+'" role="alert"><div class="bx--toast-notification__details"><h3 class="bx--toast-notification__title">'+title+'</h3><p class="bx--toast-notification__subtitle">'+subtitle+'</p><p class="bx--toast-notification__caption">'+caption+'</p></div><button data-notification-btn class="bx--toast-notification__close-button" type="button"><svg class="bx--toast-notification__icon" aria-label="close" width="10" height="10" viewBox="0 0 10 10" fill-rule="evenodd"><path d="M9.8 8.6L8.4 10 5 6.4 1.4 10 0 8.6 3.6 5 .1 1.4 1.5 0 5 3.6 8.6 0 10 1.4 6.4 5z"></path></svg></button></div>';
	
	$("body").append(notification);
		
	$('#notification-'+title).css( "margin-top", (number*100)+"px" );
	$('#notification-'+title).delay(8000).fadeOut(300);
	
	$('.bx--toast-notification__close-button').click(function(){
		$(this).parent().css("display", "none");
	});
	
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
	var valoreOptions = $(IDselector).val();
	var key = $(IDselector).data('key');
	
	console.log("This is the value of IDselector: " + valoreOptions);
	console.log("This is the value of result : " + JSON.stringify(result));
	console.log("This is the value of testdetails : " + JSON.stringify(testdetails));
	
	if(!hideresult){
		drawRocCurves(valoreOptions);
		drawIndexes(valoreOptions);
	}
	//serve per distinguere history (key==null) da simulation (key!=null)
//	if( key != null ){
				
				for( var f in result ){
					
					if( result[f].name == valoreOptions ){
						
						console.log("*************************************")
						console.log("This is the value of name: " + result[f].name )
						
						console.log("****FOUND ***")
						
						console.log("This is what I'm passing to setParameters: " + JSON.stringify(result[f]) )
						console.log("... This should be accuracy: " + result[f].accuracyOpt.toFixed(2) )
						
						setParameters(result[f]);
							
						var negative_images = [];
						for(var i=0;i<result[f].falseNegativeOpt.length;i++) 
							negative_images.push("GetImage?image_id="+result[f].falseNegativeOpt[i]);
						$("#galleryFN").empty()
						createGallery('galleryFN',negative_images,"showtestNEG");
						
						var positive_images = [];
						for(var i=0;i<result[f].falsePositiveOpt.length;i++) 
							positive_images.push("GetImage?image_id="+result[f].falsePositiveOpt[i]);
						$("#galleryFP").empty()
						createGallery('galleryFP',positive_images,"showtestPOS");

						DrawHistogram(result[f].histogramNegative,result[f].histogramPositive);
						
					} //Closing if statement on result
			
		}
		
//	}else{
//		
//		for ( var j in testdetails) {
//			
//			if (testdetails[j].name == valoreOptions) {
//				setParameters(result[j]);
//				
//				var negative_images = [];
//				for(var i=0;i<result[j].falseNegativeOpt.length;i++) 
//					negative_images.push("GetImage?image_id="+result[j].falseNegativeOpt[i]);
//				$("#galleryFN").empty()
//				createGallery('galleryFN',negative_images,"showtestNEG");
//				
//				var positive_images = [];
//				for(var i=0;i<result[j].falsePositiveOpt.length;i++) 
//					positive_images.push("GetImage?image_id="+result[j].falsePositiveOpt[i]);
//				$("#galleryFP").empty()
//				createGallery('galleryFP',positive_images,"showtestPOS");
//				
//				DrawHistogram(result[j].histogramNegative,result[j].histogramPositive);
//				
//			}
	
//		}
		
//	}
	
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

		var objJSON = result[i];
		
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

	for(var i in result){
		var obj = result[i];
		var x = [];
		var y = [];
		for(var j in obj.fprTrace) x.push(obj.fprTrace[j]);
		for(var j in obj.tprTrace) y.push(obj.tprTrace[j]);
		var objJSON = result[count];
		
		
			if (result[count].name == testname) {

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
	
			setAccuracy( result.accuracyOpt.toFixed(2) );
			setTreshold( result.thresholdOpt.toFixed(2) );
			setAUC( result.AUC.toFixed(2) );
			
//			console.log("Setting ACCURACY: " + result.accuracyOpt.toFixed(2)+ ", TRESHOLD: " + result.thresholdOpt.toFixed(2) + ", AUC: " + result.AUC.toFixed(2))
			
			$("#accuracy").empty();
			$("#threshold").empty();
			$("#accuracy").html(result.accuracyOpt.toFixed(2));
			$("#threshold").html(result.thresholdOpt.toFixed(2));
			$("#auctest").html(result.AUC.toFixed(2));
}

var acc, tres, auc;

function setAccuracy( a ){
	
	acc = a;
	
}

function getAccuracy(){

	console.log("Retrieving ACCURACY: " + acc)
	return acc;

}

function setTreshold( t ){
	
	tres = t;
	
}

function getTreshold(){
	
	console.log("Retrieving TRESHOLD: " + tres)
	return tres;
	
}

function setAUC( au ){
	
	auc = au;
	
}

function getAUC(){
	
	console.log("Retrieving AUC: " + auc)
	return auc;
	
}

function createMyModal(){
	
	var testdetails = JSON.parse(localStorage.getItem("listJSON"));
	var toBeImplemented= "";
	var testNames = [];
	
	var modal = new tingle.modal({
	    footer: true,
	    stickyFooter: false,
	    closeMethods: ['overlay', 'escape'],
	    closeLabel: "Close",
	    //cssClass: ['custom-class-1', 'custom-class-2'],
	    onOpen: function() {
	    	
//	    	toBeImplemented = "<h3> Select test to be saved: </h3>";
	    	
	    },
	    onClose: function() {
	    
			        

	    },
	    beforeClose: function() {
	        // here's goes some logic
	        // e.g. save content before closing the modal
	    	
	    	
	        return true; // close the modal
	    	return false; // nothing happens
	    }
	});

	
	for(var i in testdetails){
		
		var obj = testdetails[i];
		testNames[i] = obj.name;

	}
	
	var flag = false;
	
	for( i = 0; i < testdetails.length; i++ ){
		
		if(!flag){
		
			toBeImplemented = "<h1 style='color:#5a6872'> Select test to be saved: </h1> <br> <input style='color:#5a6872; width:25px; height:25px' value='" + testNames[i] + "' class='formcheckbox' type='checkbox' ><span style='color:#5a6872'>" + testNames[i] + "</span><br><br>";
			flag = true;
			
		}
		else
			toBeImplemented = toBeImplemented + "<input style='color:#5a6872; width:25px; height:25px;' value='" + testNames[i] + "' class='formcheckbox' type='checkbox' ><span style='color:#5a6872'>" + testNames[i] + "</span><br><br>"; 
		
	}
	
	modal.addFooterBtn('SAVE', 'tingle-btn tingle-btn--primary', function() {
		// here goes some logic
		printTestResults();
		modal.close();
		
	});
	
	// add another button
	modal.addFooterBtn('CANCEL!', 'tingle-btn tingle-btn--danger', function() {
	    // here goes some logic
	    modal.close();
	});
	
	
	openIt(modal, toBeImplemented);
	
	console.log(toBeImplemented)
	
}

function openIt(modal, content){
	
	modal.setContent(content);
	modal.open();
	
}

function openModal2() {
	populateListTestResult();
	$('#savetestmodal').fadeIn(100);
	$('#savetestmodalbackground').fadeIn(100);
	$('#savetestmodalcontent').fadeIn(100);
}

function closeModal2() {
	$('#savetestmodal').fadeOut(100);
	$('#savetestmodalbackground').fadeOut(100);
	$('#savetestmodalcontent').fadeOut(100);
}


/**
 * @param detail (_id,shortname,description, ...)
 * @param ID
 * @returns the selected detail of the classifier
 */
function returnClassifierDetail(ID, param){
	
	var tiramifuori;
	
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		url: 'GetClassifier',
		data: "_id="+ID,
		async: false,
		success: function(result)
		{
			tiramifuori = result[0][param];			
		}
	});
	
	return tiramifuori;
	
}


/**
 * @param detail (_id,shortname,description, ...)
 * @param ID
 * @returns the selected detail of the classifier
 */
function returnDatasetDetail(ID, param){
	
	var tiramifuori;
	
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		url: 'GetDataset',
		data: "_id="+ID,
		async: false,
		success: function(result)
		{
			console.log(result[0][param]);
			tiramifuori = result[0][param];			
		}
	});
	
	return tiramifuori;
	
}



/**
 * @param 
 * @param 
 * @returns
 */
function updateClassifierDetail(ID, shortName, label, descr, comm){
			
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		url: 'UpdateClassifier',
		data: "_id="+ID+"&shortname="+shortName+"&label="+label+"&description="+descr+"&comments="+comm,
		async: true,
		success: function(result)
		{
		},
		complete: function (data) {
	    
			workingUpdate(true); 
	     
		}

	
	});
		
}

function updateDataset(id,label,description,comment){
	console.log("[function.js - updateDataset] Function called")
	$.ajax({												
		contentType: "application/json",
		dataType: "json",
		url: 'UpdateDataset',
		data: "_id="+id+"&label="+label+"&description="+description+"&comments="+comment,
		async: true,
		success: function(result)
		{
		},
		complete: function (data) {
	    
			workingUpdate(true); 
	     
		}

	});
	
}

function workingUpdate(flag){
	
	if( flag ){
			
	 		swal({
				  title: "Saved!",
				  html: "Your changes have been saved correctly!<br><br>",
				  type: "success",
				  customClass: 'modal-container',
				  showCancelButton: false,
				  buttonsStyling: false,
                  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
				  confirmButtonText: 'Ok',
				  allowOutsideClick: false,
				  allowEscapeKey: true
				}).then(function(result) {
				
					if(result) ; //do nothing!
				
				})
				
		}
	
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
				x.setAttribute("id", result[i].substring(result[i].indexOf("=") + 1) );
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
								+ "</div><id-image>"+gian_str+"</id-image><img class='modal-img' data-src="
								+ img_path + result[i] + "></div>");
				
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
		
	} else {
		var i;
		var slides = document.getElementsByClassName("mySlides" + GALLERY);
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
 * POPULATION OF APIs KEYS TABLE IN PROFILE PAGE
 */
function populateAPITable(){

	//chiamata ajax per ottenere le istanze
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		url: 'GetInstance',
		async: true,
		success: function(result){
			
			var table = document.getElementById('APITable');
			
			for(var i in result)
			{
				var tr = document.createElement("tr");
				
				tr.setAttribute("tabindex","0");
				tr.setAttribute("class","bx--table-row bx--parent-row");
				tr.setAttribute("data-parent-row", "");
				
				
				var td1 = document.createElement("td");
				var td2 = document.createElement("td");
				var td3 = document.createElement("td");
				
				td1.style.cssText = 'text-align:center;font-size:16px;font-weight:700';
				td2.style.cssText = 'text-align:center;font-size:16px';
				td3.style.cssText = 'text-align:center;padding:6px';
				
				var text1 = document.createTextNode(result[i].classifiers.length);
				var text2 = document.createTextNode(result[i].api_key);
				
				var btn = document.createElement('input');
				btn.type = "button";
				btn.className = "bx--btn bx--btn--primary";
				btn.value = "delete";
				//vector with all the info needed to delete the instance
				// [0] instance id
				// [1]....[n] all the classifiers linked to the instance
				

				
					var instanceId=result[i]._id;
					var classifierIds=result[i].classifiers;
		
				btn.onclick = (function(instanceId,classifierIds) {return function() {

					swal({
						  title: "Are you sure?",
						  html: "You are deleting also all classifiers ("+classifierIds.length+") linked to this api_key. Are you really sure you want to delete this instance?<br><br>",
						  type: "warning",
						  customClass: 'modal-container',
		                  confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
						  confirmButtonText: 'Delete!',
						  cancelButtonClass: 'bx--btn bx--btn--secondary margin-lr',
						  cancelButtonText: 'Cancel',
						  buttonsStyling: false,
						  showCancelButton: true,
						  allowOutsideClick: false,
						  allowEscapeKey: true
						}).then( function(result) {
							
							if(result){
								
							$.ajax({


							   	contentType : "application/json",
							   	dataType : "json",
							  	data : {instance: instanceId, classifier: JSON.stringify(classifierIds)},
							  	url : 'DeleteInstance',
							   	async : false,
							   	success : function(result) {
								if(result.hasOwnProperty('error'))
					   				{
						   				swal({
							   					title: 'Warning',
							   					html: result.error + '<br><br>',
							   					type: 'warning',
							   					customClass: 'modal-container',
							   					showCancelButton: false,
							   					showConfirmButton: true,
							   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
												confirmButtonText: 'Got it'
						   					});
					   				}
					   		else{
					   					swal({
						   						title: 'Deleted!',
						   						html: 'This instance and all classifiers linked to it has been deleted.',
						   						type: 'success',
						   						customClass: 'modal-container',
							   					showCancelButton: false,
							   					showConfirmButton: true,
							   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
												confirmButtonText: 'Yeah!'
					   						}).then(function(){location.reload();})
					   		}
							   	}								
	   					});
							
						}	
							//User really wants to delete the classifier
//							if(result){
//								
//								for(var j in classifiers)
//									{
//									console.log(classifiers[j]);
////									TODO esiste un modo per verificare se un classificatore è in uso? In questo momento se un altro sta usando il classificatore si trova magari mezza simulazione fatta e mezza no
//									$.ajax({
//
//									   	contentType : "application/json",
//									   	dataType : "json",
//									  	data : "classifierId=" + classifiers[j] + "",
//									  	url : 'DeleteClassifier',
//									   	async : false,
//									   	success : function(result) {
////										TODO bisogno assolutamente gestire il caso in cui dia errore (esempio cancella 1 classifier e il 2 invece non riesce, a quel punto non cancello l'istanza
////										TODO si potrebbe pensare di mettere uno stato progressivo della cancellazione dei classificatori
//									   	}								
//			   					});
//									}
//								//TODO verificare se con swal interrompo l'esecuzione delle chiamate e quindi posso mettere un intermezzo ogni volta che viene cancellato un classificatore
//								$.ajax({
//									   	contentType : "application/json",
//									   	dataType : "json",
//									  	data : "instanceId=" + instanceId + "",
//									  	url : 'DeleteInstance',
//									   	async : true,
//									   	success : function(result) {
//									   		if(result.hasOwnProperty('error'))
//									   				{
//										   				swal({
//											   					title: 'Warning',
//											   					html: result.error + '<br><br>',
//											   					type: 'warning',
//											   					customClass: 'modal-container',
//											   					showCancelButton: false,
//											   					showConfirmButton: true,
//											   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
//																confirmButtonText: 'Got it'
//										   					});
//									   				}
//									   		else{
//									   					swal({
//										   						title: 'Deleted!',
//										   						html: 'Instance has been deleted.',
//										   						type: 'success',
//										   						customClass: 'modal-container',
//											   					showCancelButton: false,
//											   					showConfirmButton: true,
//											   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
//																confirmButtonText: 'Yeah!'
//									   						}).then(function(){location.reload();})
//									   		}
//									   	}
//			   					});
//								
//							}
								
						});
					}})(instanceId,classifierIds);
				
				
				td1.appendChild(text1);
				td2.appendChild(text2);
				td3.appendChild(btn);
				
				tr.appendChild(td1);
				tr.appendChild(td2);
				tr.appendChild(td3);
				
				table.appendChild(tr);
			}
		}
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
			    
			    $("#simulate").fadeOut(500);
			    setTimeout(function(){$("#waiting").fadeIn(500)},500);

				var testdetails = localStorage.getItem("listJSON");
			    
				console.log("THIS IS testdetails: {startSimulation}" + testdetails)
				
				//INSERTING NEW METHOD WHERE WE SPLIT THE AJAX CALLS
				var parsedSimulationData = JSON.parse(testdetails);
				var simulationSize = Object.keys(directJSON).length;
				
				localStorage.removeItem("resultJSON");
				
				for( var i = 0; i < simulationSize; i++ ){
					
					var questo = [];
					
					questo[0] = JSON.stringify( parsedSimulationData[i] );
					
					var manipulated = "[" + JSON.stringify( JSON.parse(questo) ) + "]";
					
					console.log("This is manipulated: " + manipulated ) 
					
					ajaxLoading.show();
					
					$.ajax(
						{
							url: 'GetTestResultMultipleAjax',
							type: 'GET',
							data:{ array: manipulated },
							dataType: 'json',
							indexValue: i,
							async: true,
							complete: function (result) {
if(result.hasOwnProperty('error'))
{
swal({
		title: 'Warning',
		html: result.error + '<br><br>',
		type: 'warning',
		customClass: 'modal-container',
		buttonsStyling: false,
			showCancelButton: false,
			showConfirmButton: true,
			confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
		confirmButtonText: 'Got it'
	});

}
else{
								ajaxLoading.hide();
								
								if( localStorage.getItem("resultJSON") === null ){
									//primo test che ritorna inizializza il local storage
									localStorage.setItem("resultJSON", JSON.stringify(result.responseJSON) );
								}
								else{
									//successivi test che ritornano vanno in append sul local storage							
									var temp = localStorage.getItem("resultJSON");			
									var other = JSON.stringify(result.responseJSON);
									var otherCorrect = (other).slice(1, -1);
									var e = JSON.parse(temp);
									e.push(JSON.parse(otherCorrect) );								
									localStorage.setItem("resultJSON", JSON.stringify(e) );					
								}
								
								console.log( "I HAVE FINISHED AJAX CALL #" + this.indexValue ) 
								$("#waiting").fadeOut(1000); //ogni voglia bisogna fare il fadeout??
								$("#showtest").fadeIn(2000); //ogni voglia bisogna fare il fadein??
								
								//aggiorno il menù a tendina dei test con il nuovo risultato
								var last = result.responseJSON.length-1;
								console.log(result.responseJSON[last].notification)
								switch(result.responseJSON[last].notification) {
							    case "error":
							    	buildSelectTestResult('#show_test', 1);
							        break;
							    case null:
							    	buildSelectTestResult('#show_test', 2);
							        break;
							    case "success":
							    	buildSelectTestResult('#show_test', 0);
							    default:
							    	console.log("[startsimulation] WARNING: beahiour not handled!!")

							} 
								
								updateTestFields('#show_test');				
							}
							}
						});
					
					
					
				}
				

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

var list = [];
var k = 0;
var IDselector;

function populateFilterDataset(ID_s){
	
	IDselector = ID_s;
	
	$.ajax({													
		dataType: "json",
		url: 'GetDataset',
		data: "_id=",
		async: false,
		success: function(result){
			
			for(var i in result){
				
				list[k] = ""+result[i].label;
				k++;
				
			}
			
		}
	
	});
	
}

function populateFiltering(){
	
	var arrayTest = [];
	var flag = false; 
	
	for( var i = 0; i < list.length; i++ ){
		
		arrayTest.push( list[i] );
		
	}
	
	//Preparing to force dataset fetcing if some error(s) occured
	if(list.length == 0){
		
		var test= [];
		
		//Forcing procedure
		test = forcingTest();
		
		flag = true;
		
	}
	
	//No error occured
	if(!flag){
		
		//Removing label duplicates
		var uniqueDataset = [];
		$.each(arrayTest, function(i, el){
		    if($.inArray(el, uniqueDataset) === -1) uniqueDataset.push(el);
		});
		
		for( var i = 0; i < uniqueDataset.length; i++ ){
			
			$("#filterDataset").append($('<option>', {
				value: uniqueDataset[i],
				text: uniqueDataset[i]
			}));	
			
		}
		
	}		
	
}

//Handling possible fetch errors during populating dataset in select in home
function forcingTest(){
	
	var newArray = [];
	var f = 0;
	
	$.ajax({													
		dataType: "json",
		url: 'GetDataset',
		data: "_id=",
		async: true,
		success: function(result){
			
			for(var i in result){
				
				newArray[f] = ""+result[i].label;
				f++;
				
			}
			
		}
	
	});
	
	var uniqueDataset = [];
	$.each(newArray, function(i, el){
	    if($.inArray(el, uniqueDataset) === -1) uniqueDataset.push(el);
	});	
	
	for( var i = 0; i < uniqueDataset.length; i++ ){
		
		$("#filterDataset").append($('<option>', {
			value: uniqueDataset[i],
			text: uniqueDataset[i]
		}));	
		
	}

	return newArray;
	
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

function fillAccountDetails(){	
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		url: 'GetInstance',
		async: true,
		success: function(result){
		$("#totalinstances").prepend(result.length);
		}
	});  
	
	$.ajax({
		contentType: "application/json",
		dataType: "json",
		url: 'GetClassifier',
		data : "_id=",
		async: true,
		success: function(result){
			$("#totalclassifiers").prepend(result.length);
		}
	
  	});

	
	$.ajax({
	contentType : "application/json",
	dataType : "json",
	url : 'GetDataset',
	data : "_id=",
	async : true,
	success : function(result) {
		$("#totaldatasets").prepend(result.length);
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
			createCircle(freeclassifiers, '#5596e6', free);
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

			createCircle(readyclassifiers, '#3d70b2', ready);
			createCircle(trainingclassifiers, '#41d6c3', training);
			createCircle(zombieclassifiers, '#8c9ba5', zombie);
			
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
//	$.ajax({
//		contentType : "application/json",
//		dataType : "json",
//		url : 'GetDataset',
//		data : "_id=",
//		async : true,
//		success : function(result) {
//
//			for ( var i in result) {
//				
//				var circle = document.createElement("div");
////				circle.setAttribute("class","smoothrectangle ready");
//				circle.setAttribute("class","bx--card large ready");
//				circle.setAttribute("id","showdatasetID"+result[i]._id);
//				circle.appendChild(document.createTextNode(result[i]._id));
//				$("#listdataset").append(circle);
//				
//			}
//		}
//	});
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
						html: result.error + '<br><br>',
						type: 'warning',
						customClass: 'modal-container',
						buttonsStyling: false,
	   					showCancelButton: false,
	   					showConfirmButton: true,
	   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
						confirmButtonText: 'Got it'
					});
				}
			else{
				swal({
						title: 'Launched!',
						html: 'Your classifier training has been launched! It will appear shortly in home page.<br><br>',
						type: 'success',
						customClass: 'modal-container',
						buttonsStyling: false,
	   					showCancelButton: false,
	   					showConfirmButton: true,
	   					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
						confirmButtonText: 'Yeah!'
					}).then(function(){window.location.href="home.html"})
		}
			}
	});
		}else{
			swal({
					title: 'Warning',
					html: 'You have to select a valid image dataset.<br><br>',
					type: 'warning',
					customClass: 'modal-container',
					buttonsStyling: false,
					showCancelButton: false,
					showConfirmButton: true,
					confirmButtonClass: 'bx--btn bx--btn--primary margin-lr',
					confirmButtonText: 'Got it'
				});
		} 	
}

/**		
* Function called in generateHome to set the height of the dataset list equal to the height of the classifier table		
*/		
function setListHeight(height){		
//	$('#listdataset').css("height", 2*height);		
	$('#listdataset').css("height", 300);			
}



/**		
* Functions called to show/hide Watson logo during ajax calls
*/	
	var ajaxLoading = {
        _requestsInProcess: 0,

        show: function () {
            if (this._requestsInProcess == 0) {
                $('#waiting_small').attr('style', 'display: block');
            }

            this._requestsInProcess++;
        },

        hide: function () {
            this._requestsInProcess--;
            if (this._requestsInProcess == 0) {
                $('#waiting_small').fadeOut('1000');
            }
        }
    };


/*
 * ==============================================================================
 * ========================= END OF AJAX CALLS =================================
 * ==============================================================================
*/