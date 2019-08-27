//  Copyright (c) 2014 Readium Foundation and/or its licensees. All rights reserved.
//  Redistribution and use in source and binary forms, with or without modification, 
//  are permitted provided that the following conditions are met:
//  1. Redistributions of source code must retain the above copyright notice, this 
//  list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright notice, 
//  this list of conditions and the following disclaimer in the documentation and/or 
//  other materials provided with the distribution.
//  3. Neither the name of the organization nor the names of its contributors may be 
//  used to endorse or promote products derived from this software without specific 
//  prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
//  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
//  OF THE POSSIBILITY OF SUCH DAMAGE

package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.readium.sdk.android.components.navigation.NavigationElement;
import org.readium.sdk.android.components.navigation.NavigationPoint;
import org.readium.sdk.android.components.navigation.NavigationTable;
import org.readium.sdk.android.launcher.model.OpenPageRequest;

import java.util.ArrayList;
import java.util.List;

public class PageListActivity extends NavigationTableActivity {
	private static final String TAG = "PageListActivity";


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String porta_aberta_epub = getIntent().getStringExtra("porta_aberta_epub");
		if (porta_aberta_epub != null && porta_aberta_epub.equals("true")) {
			final List<NavigationElement> navigationElements = flatNavigationTable(getNavigationTable(), new ArrayList<NavigationElement>());
			pageClicked(0, navigationElements, getNavigationTable());
		}
	}

	protected NavigationTable getNavigationTable() {
		NavigationTable navigationTable = null;
        if (pckg != null) {
        	navigationTable = pckg.getPageList();
        }
		return (navigationTable != null) ? navigationTable : new NavigationTable("page-list", "", "");
	}

	private void pageClicked(int arg2, List<NavigationElement> navigationElements, NavigationTable navigationTable) {
		NavigationElement navigation = navigationElements.get(arg2);
		if (navigation instanceof NavigationPoint) {
			NavigationPoint point = (NavigationPoint) navigation;
			Log.i(TAG, "Open webview at : " + point.getContent());
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constants.CONTAINER_ID, containerId);
			OpenPageRequest openPageRequest = OpenPageRequest.fromContentUrl(point.getContent(), navigationTable.getSourceHref());
			try {
				intent.putExtra(Constants.OPEN_PAGE_REQUEST_DATA, openPageRequest.toJSON().toString());
				startActivity(intent);
			} catch (JSONException e) {
				Log.e(TAG, "" + e.getMessage(), e);
			}
		}
		Toast.makeText(this, "this is item " + navigation.getTitle(),
				Toast.LENGTH_SHORT).show();
	}

}

