function applyStyles(rows) {
	var tags = document.getElementsByTagName('a');

	if (tags!=null) {
		var len=tags.length;
		for (var i=0;i<len;i++) {
			var t = tags[i];
			if (t.href!=null && t.href.indexOf('forumdisplay.php?f=')!=-1) {
				if (t.innerText!=null && t.parentNode!=null) {
					var txt = t.innerText;
					for (var h=0; h < rows.length; h++) {
						if (txt.indexOf(rows[h].forum)!=-1) {
							applyRowFeatures(t.parentNode.parentNode, rows[h]);
							continue;
						}
					}					
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
