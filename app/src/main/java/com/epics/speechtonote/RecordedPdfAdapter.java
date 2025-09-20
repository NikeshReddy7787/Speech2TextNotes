package com.epics.speechtonote;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecordedPdfAdapter extends RecyclerView.Adapter<RecordedPdfAdapter.PdfViewHolder> {

    private Context context;
    private List<File> pdfList;

    public RecordedPdfAdapter(Context context, List<File> pdfList) {
        this.context = context;
        this.pdfList = pdfList;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        File pdfFile = pdfList.get(position);
        holder.fileNameButton.setText(pdfFile.getName());
        holder.fileNameButton.setOnClickListener(v -> openRecordedPdf(pdfFile)); // Updated here
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public class PdfViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        Button fileNameButton;

        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameButton = itemView.findViewById(R.id.fileNameButton);
            fileNameButton.setOnLongClickListener(this); // Set long click listener
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                File file = pdfList.get(position);
                deletePdf(file, position);
                return true;
            }
            return false;
        }
    }

    private void openRecordedPdf(File file) {
        // Get URI for the file using FileProvider
        Uri pdfUri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider", file);

        // Create an intent to view the PDF
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(pdfUri, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Create a chooser to allow the user to select a PDF viewer app
        Intent chooserIntent = Intent.createChooser(pdfIntent, "Open PDF with");

        // Check if there is an activity to handle the intent
        if (chooserIntent.resolveActivity(context.getPackageManager()) != null) {
            try {
                // Start the chooser activity
                context.startActivity(chooserIntent);
            } catch (ActivityNotFoundException e) {
                // Handle error if no PDF viewer app is found
                Log.e("RecordedPdfAdapter", "PDF viewer not found: " + e.getMessage());
                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Notify the user if no activity is found to handle the intent
            Log.e("RecordedPdfAdapter", "No activity found to handle PDF viewing");
            Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }



    private void deletePdf(File file, int position) {
        if (file.delete()) {
            pdfList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pdfList.size());
            Toast.makeText(context, "PDF file deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete PDF file", Toast.LENGTH_SHORT).show();
        }
    }
}
