package com.simplemobiletools.camera.Adapter

import com.android.volley.toolbox.*
import android.util.Log
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import org.json.JSONObject


open class KnowledgeGraphAdapter {


    private var context : Context
    private val queue : RequestQueue
    private var api_key = "AIzaSyB3Z174eJU4D57v8gP3KY1qzZtQdsjcu7o"
    private var base_query = "https://kgsearch.googleapis.com/v1/entities:search"

    constructor(context : Context){
        this.context = context
        this.queue = Volley.newRequestQueue(this.context)
    }

    constructor(p0: Context, p1: RequestQueue){
        this.context = p0;
        this.queue = p1;
    }


    fun getSearchResult(term: String, handler : (term : String, response : JSONObject) -> Unit) {
        val query = constructQuery(term)
        var res : JSONObject
        val stringRequest = JsonObjectRequest(query, null, Response.Listener<JSONObject> {response ->
            handler(term, response)
        }, Response.ErrorListener {
        } )

        queue.add(stringRequest)
    }

    fun constructQuery(term : String): String{
        val query = base_query + "?query=" + term.replace(" ","+") + "&limit=1" + "&key="+ api_key
        return query
    }

}
