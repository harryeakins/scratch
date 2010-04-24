#include "warpopencl.h"

#include <stdio.h>
#include <stdlib.h>

void WarpOpenCL(
    const ShortVolume* const input_volume,
    const Warpfield* const warpfield,
    const ShortVolume* const output_volume,
    const int iterations)
{
    char* build_log;
    cl_command_queue command_queue;
    cl_context context;
    cl_device_id* devices;
    cl_int status;
    cl_mem input_volume_mem;
    cl_mem output_volume_mem;
    cl_mem warpfield_x_mem;
    cl_mem warpfield_y_mem;
    cl_mem warpfield_z_mem;
    cl_program program;
    cl_kernel kernel;
    const char* const program_source = ReadFileIntoString("WarpOpenCL.cl");
    const char* program_sources[] = { program_source };
    const cl_int width = output_volume->m_images[0].m_width;
    const cl_int height = output_volume->m_images[0].m_height;
    const cl_int depth = output_volume->m_count;
    const cl_int warp_width = warpfield->m_warp_x->m_images[0].m_width;
    const cl_int warp_height = warpfield->m_warp_x->m_images[0].m_height;
    const cl_int warp_depth = warpfield->m_warp_x->m_count;
    const cl_float warp_scale = (cl_float)(warpfield->m_scale);
    int i;
    size_t device_list_size;
    size_t build_log_size;
    const size_t global_work_size[] = { width, height, depth };
    //const size_t local_work_size[] = { 150, 1, 1 }; // 0.257500
    //const size_t local_work_size[] = { 5, 5, 5 }; // 0.246800
    //const size_t local_work_size[] = { 30, 5, 1 }; // 0.254900
    //const size_t local_work_size[] = { 15, 15, 1 }; // 0.246700
    //const size_t local_work_size[] = { 6, 6, 6 }; // 0.253900
    //const size_t local_work_size[] = { 8, 8, 4 };

    context = clCreateContextFromType(NULL, CL_DEVICE_TYPE_GPU, NULL, NULL, &status);
    if (status != CL_SUCCESS) {
        context = clCreateContextFromType(NULL, CL_DEVICE_TYPE_CPU, NULL, NULL, &status);
        if (status != CL_SUCCESS) {
            Bailout("clCreateContextFromType failed");
        }
	}

    status = clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, NULL, &device_list_size);
    if (status != CL_SUCCESS) {
        Bailout("clGetContextInfo failed");
    }
    devices = calloc(1, device_list_size);
    status = clGetContextInfo(context, CL_CONTEXT_DEVICES, device_list_size, devices, NULL);
    if (status != CL_SUCCESS) {
        Bailout("clGetContextInfo failed");
    }

    for (i = 0; i < (int)(device_list_size / sizeof (cl_device_id)); i++) {
        size_t name_size;
        char* name;
        size_t image_support_size = sizeof(cl_bool);
        cl_bool image_support;


        clGetDeviceInfo(devices[i], CL_DEVICE_NAME, 0, NULL, &name_size);
        name = calloc(1, name_size);
        clGetDeviceInfo(devices[i], CL_DEVICE_NAME, name_size, name, NULL);

        clGetDeviceInfo(devices[i], CL_DEVICE_IMAGE_SUPPORT, image_support_size, &image_support, NULL);

        /*
        printf("Name: %s\n", name);
        printf("Image support: %i\n", (int)(image_support == CL_TRUE));
        */
    }

    command_queue = clCreateCommandQueue(context, devices[0], 0, &status);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clCreateCommandQueue failed", status);
    }

    input_volume_mem = clCreateBuffer(context, CL_MEM_READ_ONLY, width * height * depth * sizeof(cl_short), NULL, &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateBuffer failed");
    }
    warpfield_x_mem = clCreateBuffer(context, CL_MEM_READ_ONLY, warp_width * warp_height * warp_depth * sizeof(cl_float), NULL, &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateBuffer failed");
    }
    warpfield_y_mem = clCreateBuffer(context, CL_MEM_READ_ONLY, warp_width * warp_height * warp_depth * sizeof(cl_float), NULL, &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateBuffer failed");
    }
    warpfield_z_mem = clCreateBuffer(context, CL_MEM_READ_ONLY, warp_width * warp_height * warp_depth * sizeof(cl_float), NULL, &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateBuffer failed");
    }
    output_volume_mem = clCreateBuffer(context, CL_MEM_WRITE_ONLY, width * height * depth * sizeof(cl_short), NULL, &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateBuffer failed");
    }

    for (i = 0; i < depth; i++) {
        status = clEnqueueWriteBuffer(
            command_queue,
            input_volume_mem,
            CL_FALSE,
            i * width * height * sizeof(cl_short),
            width * height * sizeof(cl_short),
            (input_volume->m_images + i)->m_data,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            Bailout("clEnqueueWriteBuffer failed");
        }
    }
    for (i = 0; i < warp_depth; i++) {
        status = clEnqueueWriteBuffer(
            command_queue,
            warpfield_x_mem,
            CL_FALSE,
            i * warp_width * warp_height * sizeof(cl_float),
            warp_width * warp_height * sizeof(cl_float),
            (warpfield->m_warp_x->m_images + i)->m_data,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            Bailout("clEnqueueWriteBuffer failed");
        }
        status = clEnqueueWriteBuffer(
            command_queue,
            warpfield_y_mem,
            CL_FALSE,
            i * warp_width * warp_height * sizeof(cl_float),
            warp_width * warp_height * sizeof(cl_float),
            (warpfield->m_warp_y->m_images + i)->m_data,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            Bailout("clEnqueueWriteBuffer failed");
        }
        status = clEnqueueWriteBuffer(
            command_queue,
            warpfield_z_mem,
            CL_FALSE,
            i * warp_width * warp_height * sizeof(cl_float),
            warp_width * warp_height * sizeof(cl_float),
            (warpfield->m_warp_z->m_images + i)->m_data,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            Bailout("clEnqueueWriteBuffer failed");
        }
    }

    status = clEnqueueBarrier(command_queue);
    if (status != CL_SUCCESS) {
        Bailout("clEnqueueBarrier failed");
    }

    program = clCreateProgramWithSource(
        context,
        1,
        program_sources,
        NULL,
        &status);
    if (status != CL_SUCCESS) {
        Bailout("clCreateProgramWithSource failed");
    }

    status = clBuildProgram(program, 0, NULL, "-w -cl-fast-relaxed-math", NULL, NULL);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clBuildProgram failed", status);
    }

    status = clGetProgramBuildInfo(
        program,
        devices[0],
        CL_PROGRAM_BUILD_LOG,
        0,
        NULL,
        &build_log_size);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clGetProgramBuildInfo failed", status);
    }
    build_log = calloc(1, build_log_size);
    status = clGetProgramBuildInfo(
        program,
        devices[0],
        CL_PROGRAM_BUILD_LOG,
        build_log_size,
        build_log,
        NULL);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clGetProgramBuildInfo failed", status);
    }
    printf("%s\n", build_log);

    kernel = clCreateKernel(
        program,
        "warp",
        &status);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clCreateKernel failed", status);
    }

    clSetKernelArg(kernel, 0, sizeof(cl_int), &width);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 1, sizeof(cl_int), &height);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 2, sizeof(cl_int), &depth);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 3, sizeof(cl_int), &warp_width);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 4, sizeof(cl_int), &warp_height);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 5, sizeof(cl_int), &warp_depth);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 6, sizeof(cl_float), &warp_scale);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 7, sizeof(cl_mem), &input_volume_mem);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 8, sizeof(cl_mem), &warpfield_x_mem);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 9, sizeof(cl_mem), &warpfield_y_mem);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 10, sizeof(cl_mem), &warpfield_z_mem);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }
    clSetKernelArg(kernel, 11, sizeof(cl_mem), &output_volume_mem);
    if (status != CL_SUCCESS) { Bailout("clSetKernelArg failed"); }

    for (i = 0; i < iterations; i++) {
        status = clEnqueueNDRangeKernel(
            command_queue,
            kernel,
            3,
            NULL,
            global_work_size,
            NULL, //local_work_size,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            BailoutWithOpenClStatus("clEnqueueNDRangeKernel failed", status);
        }

        status = clEnqueueBarrier(command_queue);
        if (status != CL_SUCCESS) {
            Bailout("clEnqueueBarrier failed");
        }
    }

    for (i = 0; i < depth; i++) {
        status = clEnqueueReadBuffer(
            command_queue,
            output_volume_mem,
            CL_FALSE,
            i * width * height * sizeof(cl_short),
            width * height * sizeof(cl_short),
            (output_volume->m_images + i)->m_data,
            0,
            NULL,
            NULL);
        if (status != CL_SUCCESS) {
            BailoutWithOpenClStatus("clEnqueueReadBuffer failed", status);
        }
    }

    status = clFinish(command_queue);
	if (status != CL_SUCCESS) {
	    BailoutWithOpenClStatus("clFinish failed", status);
	}

    status = clReleaseContext(context);
    if (status != CL_SUCCESS) {
        BailoutWithOpenClStatus("clReleaseContext failed", status);
    }
}

