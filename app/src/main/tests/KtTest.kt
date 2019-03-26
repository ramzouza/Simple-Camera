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
import com.android.volley.RequestQueue
import com.simplemobiletools.camera.Adapter.KnowledgeGraphAdapter
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

}





