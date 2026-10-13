# 🎨 Best Auction - Icon Assets

## 📁 File Structure

```
assets/
├── icon.svg           # Vector version (for editing)
├── icon-512.png       # High resolution for Modrinth
├── icon-128.png       # Standard size for GitHub
├── icon-64.png        # Small size
└── icon-32.png        # Favicon
```

## 🎨 Design Specifications

### Color Palette
- **Background**: Dark blue to purple gradient (#1a237e → #4a148c)
- **Diamond**: Cyan gradient (#4dd0e1 → #0097a7)
- **Hammer**: Gold gradient (#ffd54f → #ffb300)
- **Coins**: Gold (#ffd700)
- **Highlights**: White (#ffffff)

### Design Elements
1. **Main diamond**: Minecraft-style 3D diamond
2. **Auction hammer**: Golden hammer in the top right
3. **Currency coins**: Coins indicating support for multiple currencies ($, ¥, €)
4. **Sparkle effect**: Glowing particles for a premium appearance
5. **Text**: "AUCTION" lettering (optional)

## 🖼️ Converting to PNG

### Option 1: Online Tools
1. Upload the SVG to [Convertio](https://convertio.co/svg-png/)
2. Set the size to 512x512
3. Download as PNG

### Option 2: Command Line (ImageMagick)
```bash
# 512x512
magick icon.svg -resize 512x512 icon-512.png

# 128x128
magick icon.svg -resize 128x128 icon-128.png

# 64x64
magick icon.svg -resize 64x64 icon-64.png

# 32x32
magick icon.svg -resize 32x32 icon-32.png
```

### Option 3: Inkscape
```bash
# 512x512
inkscape icon.svg --export-png=icon-512.png --export-width=512 --export-height=512

# 128x128
inkscape icon.svg --export-png=icon-128.png --export-width=128 --export-height=128
```

## 📋 Usage

### Modrinth
- **Project icon**: icon-512.png
- **Gallery**: Multiple sizes supported

### GitHub
- **Repository icon**: icon-128.png
- **README badge**: icon-64.png

### Inside the Plugin
- **GUI icon**: icon-32.png
- **Notification icon**: icon-64.png

## 🎯 Icon Features

1. **Visibility**: Simple design that is recognizable even at small sizes
2. **Minecraft identity**: Diamond and pixel art styling
3. **Auction elements**: Hammer and currency clearly communicate functionality
4. **Professional appearance**: Premium colors that convey reliability
5. **International appeal**: Multiple currencies highlight multi-language support

## 🔧 Editing

The SVG file can be edited with:
- **Figma**: Browser-based design tool
- **Inkscape**: Free vector editor
- **Adobe Illustrator**: Professional option
- **VS Code**: Direct SVG editing

To adjust colors or sizes, edit the relevant parts of the SVG file and export it again.