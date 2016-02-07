package ygba.gfx;

import ygba.memory.Memory;
import ygba.memory.IORegMemory;
import ygba.memory.PaletteMemory;
import ygba.memory.VideoMemory;
import ygba.memory.ObjectMemory;

import java.awt.*;
import java.awt.image.*;

public final class GFX {
    
    public final static int
            XScreenSize = 240,
            YScreenSize = 160;
    
    private int[] pixels;
    
    private MemoryImageSource imageSource;
    private Image image;
    
    private IORegMemory iorMem;
    private PaletteMemory palMem;
    private VideoMemory vidMem;
    private ObjectMemory objMem;
    
    
    public GFX() {
        pixels = new int[XScreenSize * YScreenSize];
        
        imageSource = new MemoryImageSource(XScreenSize, YScreenSize, new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF), pixels, 0, XScreenSize);
        imageSource.setAnimated(true);
        image = Toolkit.getDefaultToolkit().createImage(imageSource);
    }
    
    public void connectToMemory(Memory memory) {
        iorMem = memory.getIORegMemory();
        palMem = (PaletteMemory) memory.getBank(0x05);
        vidMem = (VideoMemory) memory.getBank(0x06);
        objMem = (ObjectMemory) memory.getBank(0x07);
    }
    
    public void reset() {
        for (int i = 0; i < pixels.length; i++) pixels[i] = 0;
        imageSource.newPixels();
        image.flush();
    }
    
    public Image getImage() { return image; }
    
    
    public void drawLine(int y) {
        if (y < YScreenSize) {
            switch (iorMem.getVideoMode()) {
                case 0: drawMode0Line(y); break;
                case 1: drawMode1Line(y); break;
                case 2: drawMode2Line(y); break;
                case 3: drawMode3Line(y); break;
                case 4: drawMode4Line(y); break;
                case 5: drawMode5Line(y); break;
            }
        } else if (y == YScreenSize) {
            imageSource.newPixels();
        }
    }
    
    private void drawMode0Line(int yScreen) {
        boolean bg0Enabled = iorMem.isBGEnabled(0);
        boolean bg1Enabled = iorMem.isBGEnabled(1);
        boolean bg2Enabled = iorMem.isBGEnabled(2);
        boolean bg3Enabled = iorMem.isBGEnabled(3);
        boolean objEnabled = iorMem.isOBJEnabled();
        
        int bg0Priority = iorMem.getPriority(0);
        int bg1Priority = iorMem.getPriority(1);
        int bg2Priority = iorMem.getPriority(2);
        int bg3Priority = iorMem.getPriority(3);
        
        drawBGLine(yScreen);
        
        for (int p = 3; p >= 0; p--) {
            if (bg3Enabled && (bg3Priority == p)) drawBGTextModeLine(yScreen, 3);
            if (bg2Enabled && (bg2Priority == p)) drawBGTextModeLine(yScreen, 2);
            if (bg1Enabled && (bg1Priority == p)) drawBGTextModeLine(yScreen, 1);
            if (bg0Enabled && (bg0Priority == p)) drawBGTextModeLine(yScreen, 0);
            if (objEnabled) drawOBJLine(yScreen, p);
        }
    }
    
    private void drawMode1Line(int yScreen) {
        boolean bg0Enabled = iorMem.isBGEnabled(0);
        boolean bg1Enabled = iorMem.isBGEnabled(1);
        boolean bg2Enabled = iorMem.isBGEnabled(2);
        boolean objEnabled = iorMem.isOBJEnabled();
        
        int bg0Priority = iorMem.getPriority(0);
        int bg1Priority = iorMem.getPriority(1);
        int bg2Priority = iorMem.getPriority(2);
        
        drawBGLine(yScreen);
        
        for (int p = 3; p >= 0; p--) {
            if (bg2Enabled & (bg2Priority == p)) drawBGRotScalModeLine(yScreen, 2);
            if (bg1Enabled & (bg1Priority == p)) drawBGTextModeLine(yScreen, 1);
            if (bg0Enabled & (bg0Priority == p)) drawBGTextModeLine(yScreen, 0);
            if (objEnabled) drawOBJLine(yScreen, p);
        }
    }
    
    private void drawMode2Line(int yScreen) {
        boolean bg2Enabled = iorMem.isBGEnabled(2);
        boolean bg3Enabled = iorMem.isBGEnabled(3);
        boolean objEnabled = iorMem.isOBJEnabled();
        
        int bg2Priority = iorMem.getPriority(2);
        int bg3Priority = iorMem.getPriority(3);
        
        drawBGLine(yScreen);
        
        for (int p = 3; p >= 0; p--) {
            if (bg3Enabled & (bg3Priority == p)) drawBGRotScalModeLine(yScreen, 3);
            if (bg2Enabled & (bg2Priority == p)) drawBGRotScalModeLine(yScreen, 2);
            if (objEnabled) drawOBJLine(yScreen, p);
        }
    }
    
    private void drawMode3Line(int yScreen) {
        boolean bg2Enabled = iorMem.isBGEnabled(2);
        if (bg2Enabled) {
            boolean isMosaicEnabled = iorMem.isMosaicEnabled(2);
            int xMosaic = iorMem.getBGMosaicXSize();
            int yMosaic = iorMem.getBGMosaicYSize();
            
            int y = (isMosaicEnabled ? (yScreen - (yScreen % yMosaic)) : yScreen);
            
            for (int xScreen = 0; xScreen < XScreenSize; xScreen++) {
                int x = (isMosaicEnabled ? (xScreen - (xScreen % xMosaic)) : xScreen);
                
                short rgb15 = vidMem.getHalfWord(((y * XScreenSize) + x) * 2);
                pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
            }
            
            boolean objEnabled = iorMem.isOBJEnabled();
            if (objEnabled) {
                for (int p = 3; p >= 0; p--) drawOBJLine(yScreen, p);
            }
        }
    }
    
    private void drawMode4Line(int yScreen) {
        boolean bg2Enabled = iorMem.isBGEnabled(2);
        if (bg2Enabled) {
            int frameAddress = (iorMem.isFrame1Selected() ? 0xA000 : 0x0000);
            
            boolean isMosaicEnabled = iorMem.isMosaicEnabled(2);
            int xMosaic = iorMem.getBGMosaicXSize();
            int yMosaic = iorMem.getBGMosaicYSize();
            
            int y = (isMosaicEnabled ? (yScreen - (yScreen % yMosaic)) : yScreen);
            
            for (int xScreen = 0; xScreen < XScreenSize; xScreen++) {
                int x = (isMosaicEnabled ? (xScreen - (xScreen % xMosaic)) : xScreen);
                
                int colorIndex = vidMem.getByte(frameAddress + ((y * XScreenSize) + x)) & 0xFF;
                short rgb15 = palMem.getHalfWord(colorIndex * 2);
                pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
            }
            
            boolean objEnabled = iorMem.isOBJEnabled();
            if (objEnabled) {
                for (int p = 3; p >= 0; p--) drawOBJLine(yScreen, p);
            }
        }
    }
    
    private void drawMode5Line(int yScreen) {
        System.out.println("Video Mode 5 unsupported");
    }
    
    
    private void drawBGLine(int yScreen) {
        
        int bgColor = toRGBA(palMem.getHalfWord(0));
        
        int begPixel = (yScreen) * XScreenSize;
        int endPixel = (yScreen + 1) * XScreenSize;
        
        for (int xScreen = begPixel; xScreen < endPixel; xScreen++) pixels[xScreen] = bgColor;
    }
    
    private void drawBGTextModeLine(int yScreen, int bgNumber) {
        
        int characterBase = iorMem.getCharacterBaseAddress(bgNumber);
        int screenBase = iorMem.getScreenBaseAddress(bgNumber);
        
        int xSize = iorMem.getTextModeXSize(bgNumber);
        int ySize = iorMem.getTextModeYSize(bgNumber);
        int xMask = xSize - 1;
        int yMask = ySize - 1;
        
        int xOffset = iorMem.getXOffset(bgNumber);
        int yOffset = iorMem.getYOffset(bgNumber);
        
        boolean isTileDataUpsideDown = (xSize != 256);
        
        boolean is256ColorPalette = iorMem.is256ColorPalette(bgNumber);
        
        boolean isMosaicEnabled = iorMem.isMosaicEnabled(bgNumber);
        int xMosaic = iorMem.getBGMosaicXSize();
        int yMosaic = iorMem.getBGMosaicYSize();
        
        int y = (isMosaicEnabled ? (yScreen - (yScreen % yMosaic)) : yScreen);
        y = (y + yOffset) & yMask;
        
        int yOffsetToAdd = 0;
        if (isTileDataUpsideDown) {
            if (y >= 256) yOffsetToAdd = 0x0800 * 2;
            y &= 0xFF;
        }
        
        int yTileDataOffset = (y >>> 3) * 32;
        
        int tileY = y & 0x07;
        
        for (int xScreen = 0; xScreen < XScreenSize; xScreen++) {
            
            int x = (isMosaicEnabled ? (xScreen - (xScreen % xMosaic)) : xScreen);
            x = (x + xOffset) & xMask;
            
            int xOffsetToAdd = 0;
            if (isTileDataUpsideDown) {
                if (x >= 256) xOffsetToAdd = 0x0800;
                x &= 0xFF;
            }
            
            int xTileDataOffset = (x >>> 3);
            
            int tileX = x & 0x07;
            
            int tileDataOffset = ((yTileDataOffset + xTileDataOffset) * 2) + (yOffsetToAdd + xOffsetToAdd);
            
            short tileData = vidMem.getHalfWord(screenBase + tileDataOffset);
            
            int tileNumber = tileData & 0x03FF;
            if ((tileData & 0x0400) != 0) tileX = 7 - tileX; // H-Flip
            if ((tileData & 0x0800) != 0) tileY = 7 - tileY; // V-Flip
            
            if (is256ColorPalette) {
                int colorIndex = vidMem.getByte(characterBase + (tileNumber * 8*8) + (tileY * 8) + (tileX)) & 0xFF;
                
                if (colorIndex != 0) { // Not a transparent color
                    short rgb15 = palMem.getHalfWord(colorIndex * 2);
                    pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
                }
            } else {
                int colorIndex = vidMem.getByte(characterBase + (tileNumber * 8*4) + (tileY * 4) + (tileX / 2)) & 0xFF;
                
                if ((tileX & 0x01) != 0) colorIndex >>>= 4;
                else colorIndex &= 0x0F;
                
                if (colorIndex != 0) {
                    int paletteNumber = (tileData >>> 12) & 0x0F;
                    short rgb15 = palMem.getHalfWord(((paletteNumber * 16) + colorIndex) * 2);
                    pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
                }
            }
        }
    }
    
    private void drawBGRotScalModeLine(int yScreen, int bgNumber) {
        
        int characterBase = iorMem.getCharacterBaseAddress(bgNumber);
        int screenBase = iorMem.getScreenBaseAddress(bgNumber);
        
        int xySize = iorMem.getRotScalModeXYSize(bgNumber);
        int xyMask = xySize - 1;
        
        int xCoordinate = iorMem.getXCoordinate(bgNumber);
        int yCoordinate = iorMem.getYCoordinate(bgNumber);
        
        int pa = iorMem.getPA(bgNumber); // DX
        int pb = iorMem.getPB(bgNumber); // DMX
        int pc = iorMem.getPC(bgNumber); // DY
        int pd = iorMem.getPD(bgNumber); // DMY
        
        boolean wraparoundEnabled = iorMem.isWraparoundOverflow(bgNumber);
        
        int xCurrentCoordinate = (yScreen * pb) + xCoordinate;
        int yCurrentCoordinate = (yScreen * pd) + yCoordinate;
        
        for (int xScreen = 0; xScreen < XScreenSize; xScreen++) {
            
            int x = xCurrentCoordinate >> 8;
            int y = yCurrentCoordinate >> 8;
            
            if (wraparoundEnabled) {
                x &= xyMask;
                y &= xyMask;
            }
            
            if ((x >= 0) && (x < xySize) && (y >= 0) && (y < xySize)) {
                int xTile = x >>> 3;
                int yTile = y >>> 3;
                
                int tileX = x & 0x07;
                int tileY = y & 0x07;
                
                int tileNumber = vidMem.getByte(screenBase + (yTile * (xySize / 8)) + xTile) & 0xFF;
                
                int colorIndex = vidMem.getByte(characterBase + (tileNumber * 8*8) + (tileY * 8) + tileX) & 0xFF;
                
                if (colorIndex != 0) {
                    short rgb15 = palMem.getHalfWord(colorIndex * 2);
                    pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
                }
            }
            
            xCurrentCoordinate += pa;
            yCurrentCoordinate += pc;
        }
    }
    
    private void drawOBJLine(int yScreen, int priority) {
        
        int vidBase = 0x00010000;
        int palBase = 0x00000200;
        
        boolean is1DMapping = iorMem.isOBJ1DMapping();
        
        int xMosaic = iorMem.getOBJMosaicXSize();
        int yMosaic = iorMem.getOBJMosaicYSize();
        
        for (int objNumber = 127; objNumber >= 0; objNumber--) {
            int objPriority = objMem.getPriority(objNumber);
            
            if (objPriority == priority) {
                boolean isRotScalEnabled = objMem.isRotScalEnabled(objNumber);
                
                int xSize = objMem.getXSize(objNumber);
                int ySize = objMem.getYSize(objNumber);
                
                int xTiles = xSize >>> 3;
                int yTiles = ySize >>> 3;
                
                int xCoordinate = objMem.getXCoordinate(objNumber);
                int yCoordinate = objMem.getYCoordinate(objNumber);
                
                boolean is256ColorPalette = objMem.is256ColorPalette(objNumber);
                int paletteNumber = objMem.getPaletteNumber(objNumber);
                
                int firstTileNumber = objMem.getTileNumber(objNumber);
                int tileNumberIncrement;
                if (is1DMapping) {
                    tileNumberIncrement = (is256ColorPalette ? xTiles * 2 : xTiles);
                } else {
                    tileNumberIncrement = 32;
                    if (is256ColorPalette) firstTileNumber &= 0xFFFE;
                }
                
                boolean isMosaicEnabled = objMem.isMosaicEnabled(objNumber);
                
                if (!isRotScalEnabled) {
                    boolean isDisplayable = objMem.isDisplayable(objNumber);
                    
                    if (isDisplayable) {
                        boolean isHFlip = objMem.isHFlipEnabled(objNumber);
                        boolean isVFlip = objMem.isVFlipEnabled(objNumber);
                        
                        if (yCoordinate >= YScreenSize) yCoordinate -= 256;
                        
                        if ((yScreen >= yCoordinate) && (yScreen < yCoordinate + ySize)) {
                            int ySprite = yScreen - yCoordinate;
                            
                            for (int xSprite = 0; xSprite < xSize; xSprite++) {
                                int xScreen = xCoordinate + xSprite;
                                
                                if ((xScreen >= 0) && (xScreen < XScreenSize)) {
                                    int x = (isHFlip ? xSize - 1 - xSprite : xSprite);
                                    int y = (isVFlip ? ySize - 1 - ySprite : ySprite);
                                    
                                    int xTile = x >>> 3;
                                    int yTile = y >>> 3;
                                    
                                    int tileX = x & 0x07;
                                    int tileY = y & 0x07;
                                    
                                    if (is256ColorPalette) {
                                        int tileNumber = firstTileNumber + (yTile * tileNumberIncrement) + (xTile * 2);
                                        
                                        int colorIndex = vidMem.getByte(vidBase + (tileNumber * 32) + (tileY * 8) + (tileX)) & 0xFF;
                                        
                                        if (colorIndex != 0) {
                                            short rgb15 = palMem.getHalfWord(palBase + (colorIndex * 2));
                                            pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
                                        }
                                    } else {
                                        int tileNumber = firstTileNumber + (yTile * tileNumberIncrement) + (xTile);
                                        
                                        int colorIndex = vidMem.getByte(vidBase + (tileNumber * 32) + (tileY * 4) + (tileX / 2)) & 0xFF;
                                        
                                        if ((tileX & 0x01) != 0) colorIndex >>>= 4;
                                        else colorIndex &= 0x0F;
                                        
                                        if (colorIndex != 0) {
                                            short rgb15 = palMem.getHalfWord(palBase + (((paletteNumber * 16) + colorIndex) * 2));
                                            pixels[(yScreen * XScreenSize) + xScreen] = toRGBA(rgb15);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    private static int toRGBA(short rgb15) {
        
        int red   = (rgb15 & 0x001F) << 19; // >> 0  << 3 << 16
        int green = (rgb15 & 0x03E0) <<  6; // >> 5  << 3 << 8
        int blue  = (rgb15 & 0x7C00) >>> 7; // >> 10 << 3 << 0
        int alpha = 0xFF000000;
        
        return (red | green | blue | alpha);
    }
    
    
}