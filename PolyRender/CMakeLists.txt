project(PolyRender)

find_package(SDL)
if(SDL_FOUND)
    include_directories(${SDL_INCLUDE_DIR})

    add_library(
        PolyRender
        ApplyTexture.cpp
        Auto.cpp
        BairstowPolySolve.cpp
        BoundingSolid.cpp
        BumpMap.cpp
        BumpMapAdapter.cpp
        CheckerMap.cpp
        ColorFuncs.cpp
        ColorMap.cpp
        ColorSolid.cpp
        Console.cpp
        DestructionAsserter.cpp
        DiffuseLightingMask.cpp
        DrawScreen.cpp
        DummySolid.cpp
        Exception.cpp
        Fold.cpp
        GlobalWindowSize.cpp
        Inheritable.cpp
        Intersect.cpp
        IntersectForwarder.cpp
        Intersection.cpp
        JuliaSet.cpp
        Light.cpp
        Line.cpp
        LineFuncs.cpp
        LinkCount.cpp
        Maybe.cpp
        NewtonPolySolve.cpp
        Norms.cpp
        Plane.cpp
        Polynomial.cpp
        ProjectionPlane.cpp
        RenderInfo.cpp
        RenderMask.cpp
        RenderMemory.cpp
        ShadowMask.cpp
        Solid.cpp
        SolidIntersect.cpp
        SolidIntersection.cpp
        SolidUnion.cpp
        SpecularLightingMask.cpp
        Sphere.cpp
        StandardRenderMask.cpp
        TextUtilities.cpp
        TextureMask.cpp
        Timer.cpp
        TimingPool.cpp
        Torus.cpp
        TransformationAdapter.cpp
        TwirlyBump.cpp
        Utilities.cpp
        Vector3d.cpp
        VectorAdapter.cpp
        World.cpp
        stdafx.cpp
    )

    if(APPLE)
        add_executable(PolyRenderBin Main.cpp SDLMain.m)
    else(APPLE) 
        add_executable(PolyRenderBin Main.cpp)
    endif(APPLE)
    target_link_libraries(PolyRenderBin PolyRender ${SDL_LIBRARY})
else(SDL_FOUND)
    message("SDL not found!")
endif(SDL_FOUND)
