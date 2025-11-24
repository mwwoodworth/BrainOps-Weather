import os
from PIL import Image, ImageDraw, ImageFont

def create_brainops_icon(size, filename):
    # Create a background
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Draw circle background (Deep Blue/Purple BrainOps theme)
    bg_color = "#1E293B" # Dark slate
    draw.ellipse((0, 0, size, size), fill=bg_color)

    # Draw "Brain" Logic - A simplified network node graph
    # Center
    cx, cy = size / 2, size / 2
    radius = size * 0.4
    
    # Node colors
    node_color = "#38BDF8" # Light Blue
    line_color = "#38BDF8"
    
    # Draw connections
    nodes = [
        (cx, cy - radius * 0.6), # Top
        (cx - radius * 0.7, cy + radius * 0.3), # Bottom Left
        (cx + radius * 0.7, cy + radius * 0.3), # Bottom Right
        (cx, cy) # Center
    ]
    
    line_width = max(1, int(size * 0.04))
    node_radius = max(2, int(size * 0.08))

    # Connect center to outer
    for nx, ny in nodes[:3]:
        draw.line((cx, cy, nx, ny), fill=line_color, width=line_width)
    
    # Connect outer ring
    draw.line((nodes[0][0], nodes[0][1], nodes[1][0], nodes[1][1]), fill=line_color, width=line_width)
    draw.line((nodes[1][0], nodes[1][1], nodes[2][0], nodes[2][1]), fill=line_color, width=line_width)
    draw.line((nodes[2][0], nodes[2][1], nodes[0][0], nodes[0][1]), fill=line_color, width=line_width)

    # Draw nodes
    for nx, ny in nodes:
        draw.ellipse((nx - node_radius, ny - node_radius, nx + node_radius, ny + node_radius), fill="#FFFFFF", outline=node_color)

    img.save(filename)
    print(f"Generated {filename}")

# Define sizes for mipmap folders
sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192
}

base_dir = "app/src/main/res"

for folder, size in sizes.items():
    path = os.path.join(base_dir, folder)
    os.makedirs(path, exist_ok=True)
    
    # Generate standard square/circle icon (Launcher)
    create_brainops_icon(size, os.path.join(path, "ic_launcher.png"))
    create_brainops_icon(size, os.path.join(path, "ic_launcher_round.png"))

print("Icons generated successfully.")
