#include "Robo.h"
#include <Wire.h>
#include <HMC5883L_Simple.h>

// Inicializa um objeto Compass uma vez e usa-o em todos os métodos
static HMC5883L_Simple Compass;

Robo::Robo(float x1, float y1, float theta1) : x(x1), y(y1), theta(theta1) {
  Wire.begin();
  Compass.SetDeclination(-21, 16, 'W');  // Configura a declinação magnética
  Compass.SetSamplingMode(COMPASS_SINGLE); // Define o modo de amostragem
  Compass.SetScale(COMPASS_SCALE_130); // Define a escala
  Compass.SetOrientation(COMPASS_HORIZONTAL_X_NORTH); // Define a orientação do sensor
}

float Robo::GetX() {
  return x;
}

float Robo::GetY() {
  return y;
}

float Robo::GetTheta() {
  return Compass.GetHeadingDegrees();
}

void Robo::SetX(float x1) {
  x = x1;
}

void Robo::SetY(float y1) {
  y = y1;
}

void Robo::SetTheta(float theta1) {
  theta = theta1;
}
