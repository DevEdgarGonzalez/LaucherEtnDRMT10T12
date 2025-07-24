package com.actia.utilities.utilities_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Copy and delete files from external sdcard
 */
public class MediaFileFunctions {
	
	/**
	 * Delete file
	 * @param context a context
	 * @param fullname path file
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean deleteViaContentProvider(Context context, String fullname) 
    { 
      Uri uri=getFileUri(context,fullname); 

      if (uri==null) {
         return false;
      }
      
      try { 
         ContentResolver resolver=context.getContentResolver(); 

         // change type to image, otherwise nothing will be deleted 
         ContentValues contentValues = new ContentValues(); 
         int media_type = 1; 
         contentValues.put("media_type", media_type); 
         resolver.update(uri, contentValues, null, null); 

         return resolver.delete(uri, null, null) > 0; 
      } 
      catch (Throwable e) 
      { 
         return false; 
      } 
   }

   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
   private Uri getFileUri(Context context, String fullname) 
   {
      // Note: check outside this class whether the OS version is >= 11 
      Uri uri;
      Cursor cursor;
      ContentResolver contentResolver;

      try
      { 
         contentResolver=context.getContentResolver(); 
         if (contentResolver == null)
            return null;

         uri=MediaStore.Files.getContentUri("external"); 
         String[] projection = new String[2]; 
         projection[0] = "_id"; 
         projection[1] = "_data"; 
         String selection = "_data = ? ";    // this avoids SQL injection 
         String[] selectionParams = new String[1]; 
         selectionParams[0] = fullname; 
         String sortOrder = "_id"; 
         cursor=contentResolver.query(uri, projection, selection, selectionParams, sortOrder); 

         if (cursor!=null) 
         { 
            try 
            { 
               if (cursor.getCount() > 0) // file present! 
               {   
                  cursor.moveToFirst(); 
                  int dataColumn=cursor.getColumnIndex("_data"); 
                  String s = cursor.getString(dataColumn); 
                  if (!s.equals(fullname)) 
                     return null; 
                  int idColumn = cursor.getColumnIndex("_id"); 
                  long id = cursor.getLong(idColumn); 
                  uri= MediaStore.Files.getContentUri("external",id); 
               } 
               else // file isn't in the media database! 
               {   
                  ContentValues contentValues=new ContentValues(); 
                  contentValues.put("_data",fullname); 
                  uri = MediaStore.Files.getContentUri("external"); 
                  uri = contentResolver.insert(uri,contentValues); 
               } 
            } 
            catch (Throwable e) 
            { 
               uri = null; 
            }
            finally
            {
                cursor.close();
            }
         } 
      } 
      catch (Throwable e) 
      { 
         uri=null; 
      } 
      return uri; 
   }
   
   /**
    * Copy a file
    * @param source source file
    * @param dest destination file
    */
	 public void copyFile(File source,File dest){
		 Log.i("CopyFileAsync","source: "+source.getPath()+" dest: "+dest.getPath()); 		 
			      File parent=new File(dest.getParent());
			      if(!parent.exists())
                      //noinspection ResultOfMethodCallIgnored
                      parent.mkdirs();
				  InputStream in=null;
				  OutputStream out=null;
					try {
						in = new FileInputStream(source);
						out = new FileOutputStream(dest);
						  // Transfer bytes from in to out
					    byte[] buf = new byte[16384];
					    int len;
//					    long total = 0;
//					    long lenghtOfFile=source.length();
					    while ((len = in.read(buf)) > 0) {
//					    	total += len;
					        out.write(buf, 0, len);
//					        String[] values=new String[2];
//					        values[0]=""+(int)((total*100)/lenghtOfFile);
//					        values[1]="File: "+dest.getPath();
					    
					    }
					} catch (IOException e) {
						
						e.printStackTrace();
					}finally{
					    try {
					    	if(in!=null)
					    		in.close();
							if(out!=null)
								out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
}
