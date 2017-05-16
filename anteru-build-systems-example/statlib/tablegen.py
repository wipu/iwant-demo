if __name__ == '__main__':
    print ('int LookupTable[] = {')
    for i in range(1024):
        print (i**2 - 1, end=',\n')
    print ('};')