#include <iostream>
#include <google/cloud/firestore/client.h>

namespace firestore = ::google::cloud::firestore;

int main() {
  // Configura tu proyecto de Firestore y crea un cliente
  firestore::Client client(firestore::MakeFirestoreConnection(
      "nombre-de-tu-proyecto.firebaseapp.com", "nombre-de-tu-proyecto"));

  // Nombre del documento a obtener
  std::string doc_name = "nombre-del-documento";

  // Accede al documento en la colección "users"
  auto doc_ref = client.collection("users").document(doc_name);
  auto doc = doc_ref.get();

  if (!doc) {
    std::cerr << "Error al obtener el documento: " << doc.status().message()
              << "\n";
    return 1;
  }

  // Obtiene los datos del documento
  auto data = doc->data();
  if (!data) {
    std::cerr << "El documento no tiene datos.\n";
    return 1;
  }

  // Imprime los datos del documento
  std::cout << "Datos del documento:\n";
  for (const auto& field : *data) {
    std::cout << field.first << ": " << field.second.DebugString() << "\n";
  }

  // Accede a la colección secundaria "Dots"
  auto dots_coll = doc_ref.collection("Dots");

  // Agrega un nuevo documento a la colección "Dots"
  firestore::MapFieldValue new_dot_data{
    {"x", 123},
    {"y", 456}
  };
  auto new_dot_ref = dots_coll.document();
  auto new_dot_result = new_dot_ref.Set(new_dot_data);

  if (!new_dot_result) {
    std::cerr << "Error al agregar el nuevo documento: "
              << new_dot_result.status().message() << "\n";
    return 1;
  }

  std::cout << "Nuevo documento agregado a la colección 'Dots' con ID "
            << new_dot_ref.id() << "\n";

  return 0;
}