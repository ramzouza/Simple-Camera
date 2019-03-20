package com.simplemobiletools.camera.Adapter

import com.android.volley.toolbox.*
import android.util.Log
import android.content.Context
import com.android.volley.Response
import org.json.JSONObject


class KnowledgeGraphAdapter {


    private var context : Context;
    private var api_key = "AIzaSyB3Z174eJU4D57v8gP3KY1qzZtQdsjcu7o";
    private var base_query = "https://kgsearch.googleapis.com/v1/entities:search"

    public constructor(context : Context){
        this.context = context
    }



    public fun getSearchResult(term: String, handler : (term : String, response : JSONObject) -> Unit) {
        val query = constructQuery(term)
        Log.i("INFO", query);
        var res : JSONObject;
        var queue = Volley.newRequestQueue(this.context);
        val stringRequest = JsonObjectRequest(query, null, Response.Listener<JSONObject> {response ->
            handler(term, response)
        }, Response.ErrorListener {
            Log.e("ERROR", "")
        } );

        queue.add(stringRequest)
    }

    private fun constructQuery(term : String): String{
        val query = base_query + "?query=" + term.replace(" ","+") + "&limit=1" + "&key="+ api_key
        return query;
    }

}
