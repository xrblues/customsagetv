function applyStyles(rows) {
	if (!rows) return;
	console.log('SageTVHighlighter: Applying ' + rows.length + ' Styles');
	var tags = document.getElementById('threadslist').getElementsByTagName('tr');

	if (tags!=null) {
		var len=tags.length;
		for (var i=0;i<len;i++) {
			var txt = tags[i].innerText;
			for (var h=0; h < rows.length; h++) {
				if (txt.indexOf(rows[h].match)!=-1) {
					applyRowFeatures(tags[i], rows[h]);
					continue;
				}
			}					
		}
	}
}

function applyRowFeatures(tr, features) {
	if (features.hide==true||features.hide==1) {
		tr.style.display='none';
		return;
	}
	
	var els = tr.getElementsByTagName('*');
	for (var el in els) {
		if (els[el].style) {
			if (features.color) {
				els[el].style['color']=features.color;
			}
			if (features.background) {
				els[el].style['background-color'] = features.background;
			}
		}
	}	
}

function onresponse(rows) {
	applyStyles(rows);
}

chrome.extension.sendRequest({sagetv_request_data: true}, onresponse);
