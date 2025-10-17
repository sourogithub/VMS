[** Vms-desktop-client-0.0.1-RELEASE **]

[** October 29, 2019 **]
 1. If camera is added to UI but later camera gets disconnected, then "No_Video_Preview_Available" image is to be shown.

 2. "Clear View" option fails to work at times. [** resolved  October 30, 2019**]	

 3. "Clear All Views" option is the be present. [** completed October 31, 2019**]

 4. After the system reboot, camera previously deleted is not reflected. [** resolved October 30, 2019 **]
    \** resolved by adding 
	a. camera_session.xml,
	b. analytic_session.xml,
	c. id_generator.xml -> last generated camera_id (atomic-integer)
	 serialization at every delete_camera and add_camera operation **/

 5. Grabbing timestamp and camera name is not drawn for newly added cameras [** resolved October 30, 2019**]
    \** camera_bucket_get_camera(cameraID) implementation error. **/

 6. After deleting camera from the UI, video feeds were not cleaned up at times. [** resolved October 31, 2019 **]
    \** after deleting the camera, setting camera_table entry as INVALID_CHANNEL and while rendering frames.
	checking if current_tile_entry is INVALID_CHANNEL and if the last updated frame has been erased or not **/

 7. On camera disconnection, VMS must try to reconnect to camera [Stream-Master] 
    \** [** resolved November 27, 2019 **] 
	added new error status IO_ERROR, which on camera disconnect, will call Remove_Stream and try to Add_Stream 
	until SUCCESS is received,  while sleeping for 3seconds **/

 8. On System crash while camera is disconnected 
	[** resolved November 27, 2019 **]
    \** while camera is not connected, Session-Manager on wakeup() call, forks a reconnection thread for each camera
	calling Add_Stream until SUCCESS **/
 
 9. Deployed linux service [mem_management] : which runs every 5mins interval and kills the VMS-Desktop-Client application
	if Heap Memory usage reaches beyond 6.5GBs [** November 27, 2019 **]

 10. Deploying a auto-reboot script which will restart the system every Sunday at 0100 hr [** December 1, 2019 **]
