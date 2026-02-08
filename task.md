Crear un pipeline básico en GitHub Actions (.github/workflows/triggerci.yml) que
haga lo siguiente:
1. Se ejecute cuando haya un push a la rama main y se apruebe un Pull Request.
   (Aprobación por codeowner).
2. El pipeline debe tener los pasos de pruebas unitarias, Build, Analisis estático
   (coverage del 80%), prueba de análisis dinámico y de composición.
3. Compile y construya una imagen Docker de una aplicación simple (Java, NodeJs).
4. Etiquete la imagen con el commit SHA y la suba a un container registry
   (DockerHub o Quay.io).
5. Aplique un Helm chart o manifiesto YAML a un clúster de Kubernetes / OpenShift
   para desplegar la aplicación.
6. El despliegue debe seguir un enfoque GitOps: el pipeline debe actualizar un archivo
   values.yaml o un manifiesto de despliegue en el repositorio Git y permitir que
   ArgoCD sincronice los cambios hacia el clúster.
   ENTREGABLES
   • El workflow de GitHub Actions en YML.
   • Un ejemplo de values.yaml con la referencia de la imagen.
   • Arquitectura del ejercicio
   • Un manifiesto Application de ArgoCD que gestione la aplicación.
   Posteriormente realizar la sustentación del ejercicio.