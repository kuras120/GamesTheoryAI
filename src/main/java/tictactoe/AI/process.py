import sys


class Process:
    def __init__(self):
        self.__length = int(sys.argv[1])
        self.__array = sys.argv[2:-2]
        self.__points = sys.argv[-2:]

    def values(self):
        print('Macierz stanu:')
        for i in range(0, pow(self.__length, 2), self.__length):
            print('     ', self.__array[i:i + self.__length])
        print('Punkty:')
        print('     Gracz: ' + self.__points[0])
        print('     AI: ' + self.__points[1])


if __name__ == '__main__':
    process = Process()
    process.values()
