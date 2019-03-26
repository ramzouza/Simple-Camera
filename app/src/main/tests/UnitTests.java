import android.app.AlertDialog;

import org.junit.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.simplemobiletools.camera.Adapter.FirebaseVisionAdapter;
import com.simplemobiletools.camera.Adapter.KnowledgeGraphAdapter;
import com.simplemobiletools.camera.activities.MainActivity;
import com.simplemobiletools.camera.activities.ScanActivity;
import static org.mockito.Mockito.*;
import android.content.DialogInterface;

import java.util.ArrayList;

public class UnitTests {
    @Test
    public void testBasic(){
        // tests that tests work
        Assert.assertTrue(true);
    }

    @Test
    public void qrCodeTest(){
        ScanActivity scan = new ScanActivity();
        String result = "qr.com"; // example result sent to controller

        // Set up List of Builders
        ArrayList<AlertDialog.Builder> builderList = new ArrayList<>();
        int builders = 7;

        // add a bunch of builders
        for (int i=0; i<builders; i++){
            builderList.add(mock(AlertDialog.Builder.class));
        }

        // Stub out Android dependencies
        AlertDialog.Builder builder; // = mock(AlertDialog.Builder.class);
        AlertDialog.Builder nextBuilder;
        AlertDialog dialog;
        for (int i=0; i<builders-1; i++){
            builder = builderList.get(i);
            nextBuilder = builderList.get(i+1);
            when(builder.setTitle(any(String.class))).thenReturn(nextBuilder);
            when(builder.setMessage(any(String.class))).thenReturn(nextBuilder);
            when(builder.setCancelable(anyBoolean())).thenReturn(nextBuilder);
            when(builder.setPositiveButton(
                    any(String.class), any(DialogInterface.OnClickListener.class)
            )).thenReturn(nextBuilder);
            when(builder.setNegativeButton(
                    any(String.class), any(DialogInterface.OnClickListener.class)
            )).thenReturn(nextBuilder);
            dialog = mock(AlertDialog.class);
            when(builder.create()).thenReturn(dialog);
        }

        scan.handleResultController(result,builderList.get(0));

        verify(builderList.get(0)).setTitle("Link Found");
        verify(builderList.get(1)).setMessage("Do you want to visit this link: " + result +" ?");
        verify(builderList.get(2)).setCancelable(true);
    }









}
