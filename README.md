[![Code Smells][code_smells_badge]][code_smells_link]
[![Maintainability Rating][maintainability_rating_badge]][maintainability_rating_link]
[![Security Rating][security_rating_badge]][security_rating_link]
[![Bugs][bugs_badge]][bugs_link]
[![Vulnerabilities][vulnerabilities_badge]][vulnerabilities_link]
[![Duplicated Lines (%)][duplicated_lines_density_badge]][duplicated_lines_density_link]
[![Reliability Rating][reliability_rating_badge]][reliability_rating_link]
[![Quality Gate Status][quality_gate_status_badge]][quality_gate_status_link]
[![Technical Debt][technical_debt_badge]][technical_debt_link]
[![Lines of Code][lines_of_code_badge]][lines_of_code_link]

Мои лабораторные работы для BSUIR/БГУИР (белорусский государственный университет информатики и радиоэлектроники).

Предмет - COS/ЦОС (цифровая обработка сигналов).

## Условия

### Лабораторная работа 1

- Сгенерировать звуковые сигналы различной формы:
    - синусоида;
    - импульс с различной скважностью;
    - треугольная;
    - пилообразная;
    - шум.
- Сгенерировать полифонические сигналы на основе сигналов из
  предыдущего пункта (суммировать несколько монофонических сигналов).
- Сгенерировать звуковые сигналы с модуляцией параметров (амплитуда, частота) несущих сигналов, полученных в 1а при
  помощи модулирующих сигналов различной формы:
    - синусоида;
    - импульс с различной скважностью;
    - треугольная;
    - пилообразная.

### Лабораторная работа 2

Исследовать сигналы, сгенерированные в лабораторной работе №1, используя преобразования Фурье:

- Построить амплитудные и фазовые спектры сигналов, восстановить
  исходные сигналы по полученным спектрам. Использовать прямое и обратное
  дискретное преобразование Фурье (ДПФ).
- Выполнить предыдущий пункт с использованием быстрого преобразования Фурье (БПФ).
- Реализовать цифровую фильтрацию сигналов (НЧ-фильтр, ВЧфильтр, полосовой фильтр)

### Лабораторная работа 3

Реализовать фильтры свёртки для изображений:

- коробочное размытие (box blur);
- размытие по Гауссу;
- медианный фильтр;
- оператор Собеля.

### Лабораторная работа 4

Разработать программу, вычисляющую функцию корреляции двух изоб
ражений. Программа должна обеспечивать возможность расчёта функции вза
имной корреляции двух различных изображений и автокорреляционную функ
цию одного изображения. 

4а. В случае взаимной корреляции необходимо выполнить поиск фраг
мента в заданном изображении. Интерфейс программы должен содержать ис
ходное изображение, какой-либо случайный фрагмент этого же изображения, 
а также изображение корреляционной функции. В результате работы про
граммы необходимо в исходном изображении выделить найденный фрагмент 
прямоугольником. 

4б. В случае автокорреляции необходимо определить повторяющиеся 
фрагменты в заданном изображении. Интерфейс программы должен содер
жать исходное изображение и изображение автокорреляционной функции.

<!----------------------------------------------------------------------------->

[code_smells_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=code_smells

[code_smells_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[maintainability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=sqale_rating

[maintainability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[security_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=security_rating

[security_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[bugs_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=bugs

[bugs_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[vulnerabilities_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=vulnerabilities

[vulnerabilities_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[duplicated_lines_density_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=duplicated_lines_density

[duplicated_lines_density_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[reliability_rating_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=reliability_rating

[reliability_rating_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[quality_gate_status_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=alert_status

[quality_gate_status_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[technical_debt_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=sqale_index

[technical_debt_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing

[lines_of_code_badge]: https://sonarcloud.io/api/project_badges/measure?project=Hummel009_Digital-Signal-Processing&metric=ncloc

[lines_of_code_link]: https://sonarcloud.io/summary/overall?id=Hummel009_Digital-Signal-Processing
