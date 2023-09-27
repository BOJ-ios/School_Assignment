"""
@author: 김민규(1924385)
@date: 2023-09-23~26
@todo: quick sorting methods for Hoare's Partition Algorithm with different pivot choices
"""

import pandas as pd
import timeit
import random
import sys
sys.setrecursionlimit(10**6)


#!Hoare Partition
def hoare_partition(arr, left, right, method):
    if left < right:
        pivot_index = hoare_partition_process(arr, left, right, method)
        hoare_partition(arr, left, pivot_index - 1, method)
        hoare_partition(arr, pivot_index + 1, right, method)
    return arr


# !median_of ? 피봇 구하는 방법
def median_of_three_pivot(arr, left, right):
    n = right - left + 1
    if n == 1:
        return left
    elif n == 2:
        return left if arr[left] < arr[right] else right
    center = (left + right) // 2
    candidates = [(arr[left], left), (arr[center], center),
                  (arr[right], right)]
    candidates.sort()
    _, pivot_index = candidates[1]
    return pivot_index


def median_of_medians_pivot(arr, low, high):
    sub_arr = arr[low:high+1]
    n = len(sub_arr)
    if n <= 3:
        sublists = [sub_arr]
    else:
        step = n // 3
        sublists = [sub_arr[:step], sub_arr[step:2*step], sub_arr[2*step:]]
    medians = [sorted(sublist)[len(sublist)//2] for sublist in sublists]
    if len(medians) <= 3:
        pivot = sorted(medians)[len(medians)//2]
    else:
        pivot = median_of_medians_pivot(medians, 0, len(medians) - 1)
    return low + sub_arr.index(pivot)


# !옮기는 과정
def hoare_partition_process(arr, left, right, method):
    # !피봇 정하기
    if method == 0:
        pivot_index = (left + right) // 2
    elif method == 1:
        pivot_index = random.randint(left, right)
    elif method == 2:
        pivot_index = median_of_three_pivot(arr, left, right)
    elif method == 3:
        pivot_index = median_of_medians_pivot(arr, left, right)
    pivot = arr[pivot_index]

    # !옮기기 시작
    arr[left], arr[pivot_index] = arr[pivot_index], arr[left]
    left_pivot_index = left
    left += 1

    while True:
        while left <= right and arr[left] <= pivot:
            left += 1
        while arr[right] >= pivot and right >= left:
            right -= 1
        if right < left:
            break
        arr[left], arr[right] = arr[right], arr[left]

    arr[left_pivot_index], arr[right] = arr[right], arr[left_pivot_index]
    # !right는 새로운 피봇 인덱스
    return right


# !과제 엑셀 파일로 테스트
file_path = '정렬/input_quick_sort.xlsx'
data = pd.read_excel(file_path, header=None)
arr = data.iloc[:, 0].tolist()
# !랜덤 배열로 테스트
arr = [random.randint(0, 100) for _ in range(100000)]
# print(arr)


# !퀵 정렬(Hoare Partition Scheme)의 실행 시간 측정
# !Center Pivot -> Median of Three Pivot -> Median of Medians Pivot -> Random Pivot 순서
methods = {
    0: 'Center Pivot            ',
    1: 'Random Pivot            ',
    2: 'Median of Three Pivot   ',
    3: 'Median of Medians Pivot '
}

repeat_time = 1
arr_size = len(arr)
sorted_arrays = {}
result = sorted(arr.copy())
print(f'배열 크기 : {arr_size}')
print(f"시간 측정 : {repeat_time}회 반복")
time = timeit.timeit("sorted(arr.copy())",
                     globals=globals(), number=repeat_time)
print(f"python C sorted() Tim   :        {time:.3f} seconds")
for method, name in methods.items():
    time = timeit.timeit(f"hoare_partition(arr.copy(), 0, arr_size - 1, {method})",
                         globals=globals(), number=repeat_time)
    sorted_array = hoare_partition(arr.copy(), 0, arr_size - 1, method)
    sorted_arrays[method] = sorted_array
    print(f'{name}: {"Equal" if (result == sorted_array) else "Unequal"}, {time:.3f} seconds')
