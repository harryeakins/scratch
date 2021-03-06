#include <vector>
#include <string>

void VanillaFloat(const std::vector<float>& line, const std::vector<float>& kernel, std::vector<float>& out)
{
    if (line.size() != out.size()) {
        throw std::string("Invalid sizes");
    }
    if (kernel.size() % 2 != 1) {
        throw std::string("Kernel must have a single center value");
    }
    const int indexOfCenter = kernel.size() / 2;
    
    for (int i = 16; i < static_cast<int>(line.size()-16); ++i) {
        float accum = 0.0f;
        for (int j = 0; j < static_cast<int>(kernel.size()); ++j) {
            const int index = i - indexOfCenter + j;
            accum += line[index] * kernel[j];
        }
        out[i] = accum;
    }
}

void VanillaDouble(const std::vector<double>& line, const std::vector<double>& kernel, std::vector<double>& out)
{
    if (line.size() != out.size()) {
        throw std::string("Invalid sizes");
    }
    if (kernel.size() % 2 != 1) {
        throw std::string("Kernel must have a single center value");
    }
    const int indexOfCenter = kernel.size() / 2;
    
    for (int i = 16; i < static_cast<int>(line.size()-16); ++i) {
        double accum = 0.0;
        for (int j = 0; j < static_cast<int>(kernel.size()); ++j) {
            const int index = i - indexOfCenter + j;
            accum += line[index] * kernel[j];
        }
        out[i] = accum;
    }
}

