package island.creator;

import island.world.World;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CreatorWorld {

  private Scanner scanner = new Scanner(System.in);
  private Integer height;
  private Integer width;
  private Path path;
  private char[][] worldMap;


  public World create(){
    this.path = locateMapFile();
    defineSizeMap();
    this.worldMap = createBiosphere();
    return new World(height, width, worldMap);
  }



  private Path locateMapFile(){
    String fileMap;
    Path path = null;
    boolean passLocate = false;

    System.out.println("Введите название файла с картой. Пример: C:\\map.txt");
    while (!passLocate){
      fileMap = scanner.nextLine();
      path = Paths.get(fileMap);

      if (Files.exists(path)){
        passLocate = true;
      } else {
        System.out.println("Файл " + fileMap + " не обнаружен. Попробуйте ещё раз.");
      }
    }

    return path;
  }

  /*
   *
   * Определяем размер карты. Добавляем рамку для воды, чтобы не вылезти за пределы карты при исследовании островов.
   */
  private void defineSizeMap(){
    boolean passSize = false;
    System.out.println("Пожалуйста введите размер карты. В начале высоту, а затем, через пробел, ширину");

    while (!passSize){
      Scanner scanSizeMap = new Scanner(scanner.nextLine());
      try {
        height = scanSizeMap.nextInt() + 2;
        width = scanSizeMap.nextInt() + 2;

        if ( (height < 1) || (width > 5000)){
          System.out.println("Размеры даны не верно.");
        } else {
          passSize = true;
        }

      } catch (Exception e){
        System.out.println("Размеры даны не верно.");
      }
    }
  }

  /*
   *Создаём мир с заданными размерами.
   */
  private char[][] createBiosphere(){
    return createHydrosphere(new char[height][width]);
  }

   /*
    * Заполняем мир водой. Это нужно для того, чтобы избежать ошибок, если размер мира будет больше
    * мира из файла.
    */
  private char[][] createHydrosphere(char[][] worldMap){
    for (int i = 0; i < height; i++){
      for (int z = 0; z < width; z++){
        worldMap[i][z] = '~';
      }
    }

    return compileMap(worldMap);
  }

  /*
   * Читаем карту из файла и накладываем на ранее созданную карту
   */
  private char[][] compileMap(char[][] worldMap){
      try {
        if (Files.exists(path)){
          Scanner scanWorld = new Scanner(Files.newInputStream(path));

          for (int i = 1; i < height-1; i++){
            if (scanWorld.hasNext()){
              String latitude = scanWorld.nextLine();
              char[] sectors = latitude.toCharArray();
              System.arraycopy(sectors, 0, worldMap[i], 1, width-2);
            }
          }
        } else {
          System.out.println("Файл " + path.getFileName() + " не найден");
        }
      } catch (IOException ioe){
        ioe.printStackTrace();
      }
      return worldMap;
  }


}
