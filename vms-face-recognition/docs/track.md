[**] Probe Master Integration with VMS:
	
Phases : 
1. Face Recognition : [6 November 2019]
	[**] Track : 

	[JAVA Implementation :]
	1. find a way to render MJPEG, along with sending MJPEG stream to ProbeMaster 	
	2. Event POJO and dataflow from Probe-Master -> Session-Manager -> DesktopClient.
	3. Database Implementation for search event.
	4. Reflect The generated events in UI. 

	[**] Issues : 
		1. Event Buffer Structure : Decide the structure of data when event is generated in ProbeMaster api. 
		2. Slow Recognition phase for Native library. 
		3. Skip duplicate frames when successive event is generated. 
		4. Stop-Analytics : video freezes.

2. Data Training : 
		
3. Data Deletion : 



[**November 13,2019**]
1. Database implementation
2. Event Searching
3. Imageprobe -> 1.delete 2. train API

