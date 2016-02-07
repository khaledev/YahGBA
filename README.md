# YahGBA
A Nintendo GameBoy Advance emulator/debugger written in Java.

This was my first emulation project back in 2006. I took it as a learning project rather than a useful one since there are a lot of more advanced GBA emulators around the Web.

## Requirements:
- A Java Runtime Environment.
- A GBA BIOS file.
- A GBA ROM file.

## Controls:
- A -> X
- B -> C
- R -> D
- L -> S
- Select -> Space
- Start -> Enter

## Features:

### Implemented:
- ARM7TDMI CPU emulation.
	* 16-bit THUMB CPU support.
	* 32-bit ARM CPU support (still buggy).
	* CPU debugger (step by step execution, instruction disassembler, register viewer, flag viewer, switch memory bank feature).
- Memory emulation.
	* System ROM support.
	* I/O registers support.
	* Palette/video/OAM RAM support.
	* Cartridge SRAM support.
- DMA emulation.
- Timer emulation.
- GFX emulation.
	* Mode 0-4 support.
	* Backgrounds and sprites support.
	* Horizontal/vertical flipping support.
	* Horizontal/vertical offset support.
	* Horizontal/vertical mosaic effect support.
	* Rotating/scaling support (only for backgrounds).
- Misc.
	* HBlank/VBlank emulation.
	* Keypad support.
	* BIN/AGB/GBA/ZIP files support.
	* Pause and reset emulation feature.

### Unimplemented:
- Complete GFX emulation.
	* Add mode 5 support.
	* Add window 0-1 support.
	* Add window OBJ support.
	* Add fadein/fadeout effect support.
	* Add alpha-blending effect support.
	* Add rotating/scaling support for sprites.
	* Fix the remaining bugs.
- Add sound emulation.
- Add BIOS emulation (HLE).
- Add SRAM/EEPROM/flash save support.
- Add save state support.
- Add cycle-accurate timing.
- Add memory viewer, I/O registers viewer, map/OBJ/palette viewers, etc.
