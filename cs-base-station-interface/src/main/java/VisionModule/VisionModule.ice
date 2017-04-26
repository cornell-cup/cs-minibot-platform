#ifndef VISIONMODULE_ICE
#define VISIONMODULE_ICE

module VisionModule
{

	struct ColorRGB
	{
		int redValue;
		int greenValue;
		int blueValue;

	};

	struct Blob
	{
		double x;
		double y;
		int isProjectile;
		ColorRGB color1;
		ColorRGB color2;
		double orientation;
		double velocityx;
		double velocityy;
		double velocityRot;
		int botID;
		double time;
	};


	sequence<Blob> VisionData;

	interface VisionInterface
	{
		double ping(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l);
		VisionData getVision();
	};

	interface BaseInterface
	{
		double ping();
		int update(VisionData data);
	};

};

#endif
