import android.app.Activity
import android.app.AlertDialog
import com.simplemobiletools.camera.activities.ScanActivity
import org.junit.*
import org.junit.Assert
import org.junit.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.InOrder
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import org.mockito.Mockito.*
import android.content.DialogInterface
import android.util.Log
import com.android.volley.RequestQueue
import com.google.android.gms.vision.label.ImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.simplemobiletools.camera.Adapter.FirebaseVisionAdapter
import com.simplemobiletools.camera.Adapter.KnowledgeGraphAdapter
import com.simplemobiletools.camera.Utils.isHyperlink
import com.simplemobiletools.camera.activities.MainActivity
import com.simplemobiletools.camera.activities.SimpleActivity
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.mockito.Matchers

import java.util.ArrayList
public class KtUnitTests() {
    @Test fun ktTestBasic () {
        Assert.assertTrue(true)
    }

    @Test fun qrCodeTest(){
        val scan = ScanActivity()
        val result = "qr.com" // example result sent to controller

        // Set up List of Builders
        val builderList = ArrayList<AlertDialog.Builder>()
        val builders = 7

        // add a bunch of builders
        for (i in 0 until builders) {
            builderList.add(mock<AlertDialog.Builder>(AlertDialog.Builder::class.java))
        }

        // Stub out Android dependencies
        var builder: AlertDialog.Builder // = mock(AlertDialog.Builder.class);
        var nextBuilder: AlertDialog.Builder
        var dialog: AlertDialog
        for (i in 0 until builders - 1) {
            builder = builderList[i]
            nextBuilder = builderList[i + 1]
            `when`<AlertDialog.Builder>(builder.setTitle(
                    Matchers.any(String::class.java
            ))).thenReturn(nextBuilder)

            `when`<AlertDialog.Builder>(builder.setMessage(
                    Matchers.any(String::class.java)
            )).thenReturn(nextBuilder)

            `when`<AlertDialog.Builder>(builder.setCancelable(
                    Matchers.anyBoolean()
            )).thenReturn(nextBuilder)

            `when`<AlertDialog.Builder>(builder.setPositiveButton(
                    Matchers.any(String::class.java), Matchers.any(DialogInterface.OnClickListener::class.java)
            )).thenReturn(nextBuilder)

            `when`<AlertDialog.Builder>(builder.setNegativeButton(
                    Matchers.any(String::class.java), Matchers.any(DialogInterface.OnClickListener::class.java)
            )).thenReturn(nextBuilder)

            dialog = mock(AlertDialog::class.java)

            `when`(builder.create(
            )).thenReturn(dialog)
        }

        scan.handleResultController(result, builderList[0])

        verify<AlertDialog.Builder>(builderList[0]).setTitle("Link Found")
        verify<AlertDialog.Builder>(builderList[1]).setMessage("Do you want to visit this link: $result ?")
        verify<AlertDialog.Builder>(builderList[2]).setCancelable(true)
    }

    @Test
    fun knowledgeGraphQueryTest() {
        val act = mock(MainActivity::class.java);
        val queue = mock(RequestQueue::class.java);
        val adapter = KnowledgeGraphAdapter(act, queue);
        val term = "apple";
        val query = adapter.constructQuery(term);
        assertEquals(query, "https://kgsearch.googleapis.com/v1/entities:search?query=${term}&limit=1&key=AIzaSyB3Z174eJU4D57v8gP3KY1qzZtQdsjcu7o");
    }

    @Test
    fun firebaseVisionAdapterFindBestWhenAssortedOrder(){
        val act = mock(MainActivity::class.java);
        val adapter = FirebaseVisionAdapter(act);
        val collection = ArrayList<FirebaseVisionImageLabel>();

        val label = FirebaseVisionImageLabel(ImageLabel("label1","desc1",0.01f));
        val label2 = FirebaseVisionImageLabel(ImageLabel("label2","desc1",0.66f));
        val label3 = FirebaseVisionImageLabel(ImageLabel("label3","desc1",0.33f));
        val label4 = FirebaseVisionImageLabel(ImageLabel("label4","desc1",0.20f));
        collection.add(label);
        collection.add(label2);
        collection.add(label3);
        collection.add(label4);

        val best = adapter.findBest(collection);

        assertEquals(label2,best);
    }

    @Test
    fun firebaseVisionAdapterFindBestWhenOneElement(){
        val act = mock(MainActivity::class.java);
        val adapter = FirebaseVisionAdapter(act);
        val collection = ArrayList<FirebaseVisionImageLabel>();

        val label = FirebaseVisionImageLabel(ImageLabel("label1","desc1",0.50f));
        collection.add(label);

        val best = adapter.findBest(collection);

        assertEquals(label,best);
    }

    @Test
    fun firebaseVisionAdapterFindBestWhenEmpty(){
        val act = mock(MainActivity::class.java);
        val adapter = FirebaseVisionAdapter(act);
        val collection = ArrayList<FirebaseVisionImageLabel>();


        val best = adapter.findBest(collection);

        assertEquals(null, best);
    }

    @Test
    fun hyperlinkTest(){
        val links : Array<String> = arrayOf(
                "https://www.youtube.com/watch?v=HaRQaDjKShA",
                "https://github.com/ramzouza/Simple-Camera/commit/dc3b8d800c6d37f3509de6153d644f4ce7955f1a",
                "http://zetcode.com/kotlin/arrays/",
                "messenger.com",
                "www.jon.com/so-And_so@1\\t"
        )
        for (link in links){
            System.out.println(link);
            assert(isHyperlink(link))
        }
    }




}







