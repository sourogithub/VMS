/*
 * ProbeMaster.h
 *
 *  Created on: 09-Oct-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_PROBEMASTER_H_
#define INCLUDE_PROBEMASTER_H_

#include "DataTypes.h"
#include "ErrorTypes.h"
extern "C"{
ErrorTypes Initialize();

ErrorTypes Teardown();

ErrorTypes AddStream(UINT8 *dataSpace);

ErrorTypes RemoveStream(UINT8 *dataSpace);

ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer);

ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer, UINT8 *eventBuffer);

ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput);

}
#endif /* INCLUDE_PROBEMASTER_H_ */
