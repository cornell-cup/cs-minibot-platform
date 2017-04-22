#ifndef COMM_MODULE
#define COMM_MODULE

module CommModule
{


	/// from base to atom
	interface BaseInterface
	{
		// forward: -255 to 255, negative is backward movement
		// strafe: -255 to 255, positive is strafing to the right
		// rotate: -255 to 255, positive is rotating to the right
		void sendMovementData(int forward, int strafe, int rotate);
		void setMotorSpeeds(int frontLeft, int frontRight, int backLeft, int backRight);
		void moveArm(string pos);
		void sendControlInfo(int port, int on);
		void init(string ip, string name, int port, int id);
		void pokeBot(string baseIP);
		void shoot();
		void reload();
		void allOff();
		void laserTagFire();
		void laserTagShield();
		void sendSteeringData(int direction);
		void sendTurretData(int horizontal, int vertical);
		void setDuneBotMotorSpeeds(int frontLeft, int frontRight, int backRight, int backLeft);
		void requestImageProcessing();

		void restartBot();
		void shutdownBot();
	};


	/// from atom to base
	sequence<byte> ByteSeq;

	//Data returned by image processing algorithms currently used on barCodes
	struct ImgProcData
	{
		string barCodeID;
		int barCodeAngle;
		float barCodeDistance;

	};

	sequence<ImgProcData> ImgProcDataSeq;

	//
	interface SensorInterface
	{
		void initializeBot(int botnum);
		void sendLaserTagData(int botnum, string laserTagDataType, int hitCount);
		void sendStreamingVideoData(int botnum, ByteSeq bytes, int cameraID, int numObjectsDetected,  ImgProcDataSeq imgProcseq);
		void pokeBase(int botnum);
		void sendHallSensorData(int botnum, float frontLeft, float frontRight, float backLeft, float backRight);
		void sendImuData(int botnum, float yaw, float pitch, float roll, float ax, float ay, float az);
	};
};
	
#endif

