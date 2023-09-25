package com.createchance.imageeditor.transitions;

import com.createchance.imageeditor.drawers.BaseImageDrawer;

import org.libpag.PAGPlayer;
import org.libpag.PAGSurface;

public abstract class PagTransition extends AbstractTransition {
    protected static final int[] transOverlayId = new int[1];
    protected static PAGPlayer pagPlayer;
    protected static PAGSurface pagSurface;
    protected static int lastWidthSurface = 1;
    protected static int lastHeightSurface = 1;
    protected static BaseImageDrawer mDrawDefault;
    protected static String currentTransitionPath;

    public PagTransition(String name, int type) {
        super(name, type);
    }

    abstract public void loadPag();

    abstract public void loadTexture();
}
