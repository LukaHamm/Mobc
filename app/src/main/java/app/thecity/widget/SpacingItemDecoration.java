package app.thecity.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Item Dekoration für eine RecyclerView - erzeugt gleichmäßige Abstände um jedes Element in einem Rasterlayout.
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; // Anzahl der Spalten im Rasterlayout
    private int spacingPx; // Abstand zwischen den Elementen in Pixeln
    private boolean includeEdge; // Gibt an, ob die äußeren Elemente des Rasters ebenfalls Abstand haben sollen


    /**
     * Berechnet die Abstände für jedes Element im Rasterlayout basierend auf den festgelegten Parametern.
     *
     * @param outRect Rechteck zur Speicherung der Abstände
     * @param view    Ansicht des aktuellen Elements
     * @param parent  RecyclerView, das die Elemente enthält
     * @param state   Zustand der RecyclerView
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Position des Elements im Adapter
        int column = position % spanCount; // Spalte des Elements im Rasterlayout

        if (includeEdge) {
            // Wenn äußere Elemente Abstand haben sollen
            outRect.left = spacingPx - column * spacingPx / spanCount;
            outRect.right = (column + 1) * spacingPx / spanCount;

            if (position < spanCount) { // oberer Rand
                outRect.top = spacingPx;
            }
            outRect.bottom = spacingPx; // unterer Rand
        } else {
            // Wenn äußere Elemente keinen Abstand haben sollen
            outRect.left = column * spacingPx / spanCount;
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;
            if (position >= spanCount) {
                outRect.top = spacingPx; // oberer Rand
            }
        }
    }
}
