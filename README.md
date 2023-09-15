# image-stegnography

# How to run the program
1. Download repository
2. go to directory
3. add your text in input.txt you want to embed
4. "javac Main.java" on compiler
5. "java Main" on compiler


**Libraries Used:**

    java.io.File: Used for file operations, such as checking file existence.
    java.io.FileOutputStream: Used to write data to an output file.
    java.io.FileInputStream: Used to read data from an input file.
    javax.imageio.ImageIO: Used for reading and writing images.
    java.awt.image.BufferedImage: Represents an image in memory.
    java.awt.image.Raster: Represents image data.

**Embedder Class:**

The Embedder class is responsible for embedding text into an image. It takes the following parameters in its constructor:

    String password: A password used for encryption (not implemented in the code).
    String toEmbed: The path to the text file you want to embed.
    String vessel: The path to the image file (vessel) in which you want to hide the text.
    String trgtFile: The path where the resulting image with embedded text should be saved.
    BufferedImage buffVessel: The BufferedImage representation of the vessel image.

_embed Method_: This method does the actual embedding of text into the image. Here's how it works:

    It checks if the vessel image has enough capacity to embed the text.
    Creates a header containing information about the embedded file (e.g., file name and size).
    Gets the pixel matrix (raster) of the vessel image.
    Iterates over each pixel and embeds data into the RGB color channels (Red, Green, Blue).
    Updates the raster with the modified pixel data.
    Finally, it saves the modified image to the target file.

**Extractor Class:**

The Extractor class is responsible for extracting hidden text from an image. It takes the following parameters in its constructor:

    String password: A password (not used in the code).
    String vessel: The path to the image containing the hidden text.
    String trgtFolder: The folder where the extracted text file should be saved.

_extract Method_: This method does the extraction of hidden text from the image:

    Loads the vessel image into memory.
    Gets the pixel matrix (raster) of the vessel image.
    Iterates over each pixel and extracts hidden data from the RGB color channels.
    Reconstructs the hidden text from the extracted data.
    Writes the extracted text to a file in the specified target folder.

**How Text Is Embedded:**

    Text embedding is done by modifying the RGB color values of individual pixels in the image.
    Each character of the text is converted into its ASCII value, and this value is split into three parts.
    These parts are embedded into the Red, Green, and Blue color channels of the pixel, changing the least significant bits of each channel.

**How Text Is Extracted:**

    Text extraction involves reading the least significant bits of the Red, Green, and Blue color channels of each pixel.
    These bits are combined to reconstruct the ASCII values of the hidden characters.
    The extracted ASCII values are then converted back to characters to reconstruct the hidden text.
