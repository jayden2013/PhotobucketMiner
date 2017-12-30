import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Analyzes an image.
 * @author Jayden Weaver
 *
 */
public class ImageAnalyzer {
	File file;
	Color color;
	ArrayList<Color> colorArray = new ArrayList<Color>();
	double probability = 0.0;
	boolean isLikely = false;
	boolean isBetwixt = false;
	final int TOLERANCE = 8;
	//Center of SSC
	final int SSNRED = 202;
	final int SSNGREEN = 225;
	final int SSNBLUE = 219;
	//Top of SSC
	final int SSNREDTOP = 100;
	final int SSNGREENTOP = 116;
	final int SSNBLUETOP = 153;
	//SSC threshold
	final int SSNTHRESHOLD = 30000;
	final int CARDPERCENTAGE = 1000;
	final int BETWIXTVALUE = 15000;
	double confidence = 0.0;
	int height = 0;
	int width = 0;
	int numPixels = 0;
	int topPixels = 0;

	public ImageAnalyzer(File f){
		this.file = f;
		try {
			BufferedImage image = ImageIO.read(file);
			int x = 0, y = 0;
			this.height = image.getHeight();
			this.width = image.getWidth();
			this.numPixels = this.height * this.width;
			this.topPixels = numPixels / 3; //the top portion of a SSC is about a thirdish.

			while (y < this.height){
				while (x < this.width){
					int colorVal = image.getRGB(x, y);
					this.color = new Color(colorVal);
					this.colorArray.add(color);
					x++;
				}
				y++;
				x = 0;
			}

			colorAnalyzer(colorArray);

		} catch (IOException e) {
			System.err.println("Failed to load image.");
		}
	}

	/*
	 * Social Security Card Color:
	 * R: 202 G: 225 B: 219 Center Portion
	 * R: 100 G: 116 B: 153 Top Portion
	 * R: 90 G: 104 B: 140 Top Portion
	 */

	/**
	 * Analyzes the image.
	 * @param colorArray
	 */
	private void colorAnalyzer(ArrayList<Color> colorArray){
		for (int i = 0; i < colorArray.size(); i++){
			int red = colorArray.get(i).getRed();
			int green = colorArray.get(i).getGreen();
			int blue = colorArray.get(i).getBlue();

			if (i <= topPixels){
				//Check top portion
				if (red > SSNREDTOP - TOLERANCE && red < SSNREDTOP + TOLERANCE && green > SSNGREENTOP - TOLERANCE && green < SSNGREENTOP + TOLERANCE && blue > SSNBLUETOP - TOLERANCE && blue < SSNBLUETOP + TOLERANCE){
					this.probability += 2;
					if (red > SSNREDTOP - TOLERANCE / 2 && red < SSNREDTOP + TOLERANCE / 2){
						this.probability += 2;
						if (red == SSNREDTOP){
							this.probability += 2;
						}
					}
				}
				else{
					if (red > SSNREDTOP - TOLERANCE && red < SSNREDTOP + TOLERANCE){
						this.probability++;
						if (red > SSNREDTOP - TOLERANCE / 2 && red < SSNREDTOP + TOLERANCE / 2){
							this.probability++;
							if (red == SSNREDTOP){
								this.probability++;
							}
						}
					}
					if (green > SSNGREENTOP - TOLERANCE && green < SSNGREENTOP + TOLERANCE){
						this.probability++;
						if (green > SSNGREENTOP - TOLERANCE / 2 && green < SSNGREENTOP + TOLERANCE / 2){
							this.probability++;
							if (green == SSNGREENTOP){
								this.probability++;
							}
						}
					}
					if (blue > SSNBLUETOP - TOLERANCE && blue < SSNBLUETOP + TOLERANCE){
						this.probability++;
						if (blue > SSNBLUETOP - TOLERANCE / 2 && blue < SSNBLUETOP + TOLERANCE / 2){
							this.probability++;
							if (blue == SSNBLUETOP){
								this.probability++;
							}
						}
					}			
				}
			}
			else{
				//Check Center portion
				if (red > SSNRED - TOLERANCE && red < SSNRED + TOLERANCE){
					this.probability++;
					if (red > SSNRED - TOLERANCE / 2 && red < SSNRED + TOLERANCE / 2){
						this.probability++;
						if (red == SSNRED){
							this.probability++;
						}
					}
				}
				if (green > SSNGREEN - TOLERANCE && green < SSNGREEN + TOLERANCE){
					this.probability++;
					if (green > SSNGREEN - TOLERANCE / 2 && green < SSNGREEN + TOLERANCE / 2){
						this.probability++;
						if (green == SSNGREEN){
							this.probability++;
						}
					}
				}
				if (blue > SSNBLUE - TOLERANCE && blue < SSNBLUE + TOLERANCE){
					this.probability++;
					if (blue > SSNBLUE - TOLERANCE / 2 && blue < SSNBLUE + TOLERANCE / 2){
						this.probability++;
						if (blue == SSNBLUE){
							this.probability++;
						}
					}
				}
			}			
		}

		//calculate confidence level
		getConfidence();

		//Set isLikely
		if (this.confidence < this.CARDPERCENTAGE){
			this.isLikely = true;
		}
		else{
			this.isLikely = false;
		}

		//If the confidence is close to the card percentage threshold and not set as likely, set betwixt
		if (this.CARDPERCENTAGE > this.confidence - this.BETWIXTVALUE && !this.isLikely){
			this.isBetwixt = true;
		}
	}

	/**
	 * Returns true if the image was a close call.
	 * @return
	 */
	public boolean isBetwixt(){
		return this.isBetwixt;
	}

	/**
	 * Returns true if the image is a possible social security card.
	 * @return isLikely
	 */
	public boolean isSSN(){
		return this.isLikely;
	}

	/**
	 * Set whether an image is a possible social security card.
	 * @param value
	 */
	public void setSSN(boolean value){
		this.isLikely = value;
	}

	/**
	 * Returns a confidence level between 0 and 100.
	 * @return confidence
	 */
	public double getConfidence(){
		this.confidence = (this.SSNTHRESHOLD - this.probability);
		return this.confidence;
	}

	/**
	 * Returns an array of colors.
	 * @return
	 */
	public ArrayList<Color> getColorArray(){
		return this.colorArray;
	}

	/**
	 * Returns the color of the last pixel.
	 * @return
	 */
	public Color getLastColor(){
		return this.color;
	}
}
